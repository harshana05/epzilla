/***************************************************************************************************
Copyright 2009 - 2010 Harshana Eranga Martin, Dishan Metihakwala, Rajeev Sampath, Chathura Randika

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
****************************************************************************************************/
package net.epzilla.stratification.restruct;

import net.epzilla.stratification.dynamic.SystemVariables;
import net.epzilla.stratification.query.BasicQueryParser;
import net.epzilla.stratification.query.InvalidSyntaxException;
import net.epzilla.stratification.query.Query;
import net.epzilla.stratification.query.QueryParser;
import org.epzilla.dispatcher.dispatcherObjectModel.TriggerInfoObject;
import org.epzilla.util.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

/**
 * keeps track of the structure of the triggers and manages it.
 */
public class TriggerStrcutureManager {

    LinkedList<Query> qlist = new LinkedList<Query>();
    HashSet<Integer>[] map = null;
    boolean[] present = null;
    HashMap<String, HashSet<Integer>> outMap = null;
    LinkedList<LinkedList<Integer>> strata = null;
    String clientId;
    LinkedList<LinkedList<Cluster>> mapping = null;
    List<TriggerInfoObject> trList = null;


    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public synchronized void addQuery(Query q) {
        qlist.add(q);
    }

    public synchronized void addQueries(List<Query> list) {
        qlist.addAll(list);
    }

    /**
     * reorganize the trigger base.
     */
    public void restructure() {
        HashMap<Integer, HashMap<Integer, Cluster>> m = new HashMap();

        int[][] lmap = new int[SystemVariables.getNumStrata()][];
        for (int i = 0; i < lmap.length; i++) {
            lmap[i] = new int[SystemVariables.getClusters(i)];
        }

        for (LinkedList<Cluster> cl : mapping) {
//            System.out.println("sz:" + cl.size());
            for (Cluster c : cl) {
                HashMap<Integer, Cluster> hm = m.get(c.getStratum());
//                System.out.println("getst:" + c.getStratum());
                if (hm == null) {
                    hm = new HashMap();
                    m.put(c.getStratum(), hm);
                }
                hm.put(c.getCluster(), c);
                if (!c.isIndependent()) {
                    lmap[c.getRealStratum()][c.getRealCluster()] += c.getLoad();
                }
            }
        }

        for (Query q : qlist) {
            Cluster c = m.get(q.getStratum()).get(q.getCluster());
//            System.out.println("gettin:" + q.getCluster());
            q.setStratum(c.getRealStratum());
            q.setCluster(c.getRealCluster());
        }

        Iterator<TriggerInfoObject> i = trList.iterator();
        Iterator<Query> j = this.getQueryList().iterator();

        int[] s = new int[SystemVariables.getNumStrata()];

        int[] lim = new int[s.length];
        for (int ii = 0; ii < lim.length; ii++) {
            lim[ii] = SystemVariables.getClusters(ii) - 1;
        }

        while (i.hasNext()) {
            TriggerInfoObject obj = i.next();
            Query qo = j.next();
            obj.setoldClusterId(obj.getclusterID());
            obj.setoldStratumId(obj.getstratumId());
            int st = qo.getStratum();
            if (qo.isIndependent()) {
                int ts = getMin(lmap[qo.getStratum()]);
//                System.out.println("min:" + ts);
                if (s[st] > lim[st]) {
                    s[st] = 0;
                }
                obj.setclusterID(String.valueOf(ts));
                lmap[qo.getStratum()][ts]++;

            } else {
                obj.setclusterID(String.valueOf(qo.getCluster()));
            }
            obj.setstratumId(String.valueOf(qo.getStratum()));
        }

    }

    private int getMin(int[] ar) {
        int m = 0;
        int i = 0;
        for (int x : ar) {
            if (x < ar[m]) {
                m = i;
            }
            i++;
        }
        return m;
    }

    /**
     * assign a virtual stratum for each query.
     *
     * @return
     */
    public LinkedList<LinkedList<Integer>> markStrata() {
        int[] st = new int[qlist.size()];
        int i = 0;
        for (LinkedList<Integer> list : strata) {
            for (Integer in : list) {
                st[in] = i;
            }
            i++;
        }
        i = 0;
        for (Query q : qlist) {
            q.setStratum(st[i]);
            i++;
        }

        return strata;
    }

    public LinkedList<Query> getQueryList() {
        return this.qlist;
    }


    /**
     * returns the virtual trigger structure which contains many virtual strata and clusters.
     *
     * @param triggerList
     * @return
     * @throws InvalidSyntaxException
     */
    public LinkedList<LinkedList<Cluster>> getVirtualStructure(List<TriggerInfoObject> triggerList) throws InvalidSyntaxException {
        LinkedList<Query> list = new LinkedList();
        trList = triggerList;
        QueryParser qp = new BasicQueryParser();
        Query q = null;
        for (TriggerInfoObject tio : triggerList) {
            if (!"OOOO".equals(tio.gettrigger())) {
                q = qp.parseString(tio.gettrigger());
                Logger.log("tid:" + tio.gettriggerID());
                q.setId(Integer.parseInt(tio.gettriggerID()));
                list.add(q);
            }
        }

        this.addQueries(list);
        this.buildGraph();
        List<LinkedList<Integer>> lx = this.markStrata();
        LinkedList<LinkedList<Cluster>> clist = new LinkedList();

        // mark clusters.
        Clusterizer c = null;
        int i = 0;
        for (LinkedList<Integer> st : lx) {
            c = new Clusterizer();
            c.clusterize(st, this.getQueryList(), clientId, i, false);
            i++;
            clist.add(c.getVirtualClusterInfo());
//            for (Cluster ccx: c.getVirtualClusterInfo()) {
//                System.out.println("vcluster:" + ccx.getCluster());
//            }

        }
        mapping = clist;
        return clist;


    }


    /**
     * builds the query dependency graph.
     */
    public synchronized void buildGraph() {
        map = new HashSet[qlist.size()];
        present = new boolean[qlist.size()];
        strata = new LinkedList<LinkedList<Integer>>();
        Arrays.fill(present, true);


        buildOutMap();
        buildDependencyMap();

        while (true) {

            LinkedList<Integer> list = new LinkedList<Integer>();

            for (int i = 0; i < map.length; i++) {
                if (present[i]) {
                    HashSet<Integer> set = map[i];
                    if (set.size() == 0) {
                        list.add(i);
//                        present[i] = false;
                    } else {
                        boolean hasDependency = false;
                        for (Integer val : set) {
                            if (present[val]) {
                                hasDependency = true;
                                break;
                            }
                        }

                        if (!hasDependency) {
                            list.add(i);
                        }
                    }
                }
            }

            if (list.size() == 0) {
                break;
            } else {
                this.strata.add(list);
                for (Integer i : list) {
                    present[i] = false;
                }
            }
        }


    }

    private void buildDependencyMap() {
        long st = System.currentTimeMillis();

        int i = 0;
        for (Query q : qlist) {
            String[] in = q.getInputs();
            HashSet set = new HashSet<Integer>();
            HashSet temp = null;
            for (String item : in) {
                temp = outMap.get(item);
                if (temp != null) {
                    set.addAll(temp);

//                    Collections.addAll(set, temp);
                }
            }
            this.map[i] = set;
            i++;
        }
        Logger.log("depend built: " + (System.currentTimeMillis() - st));

    }

    private void buildOutMap() {
        // building out map.
        this.outMap = new HashMap();
        int i = 0;
        HashSet<Integer> set = null;
        for (Query q : qlist) {
            String[] out = q.getOutputs();
            for (String item : out) {
                set = outMap.get(item);
                if (set == null) {
                    set = new HashSet<Integer>();
                    outMap.put(item, set);
                }
                set.add(i);
            }
            i++;
        }
    }
}
