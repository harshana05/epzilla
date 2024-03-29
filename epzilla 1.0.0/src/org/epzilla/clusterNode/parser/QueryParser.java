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
package org.epzilla.clusterNode.parser;

import org.epzilla.clusterNode.query.Query;
import org.epzilla.clusterNode.query.QuerySyntaxException;
import org.epzilla.clusterNode.query.QueryType;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: Rajeev
 * Date: Mar 8, 2010
 * Time: 8:44:51 AM
 * To change this template use File | Settings | File Templates.
 */
public class QueryParser {
    static String[] operators = new String[]{">=", "<=", "!=", "<", ">", "="};
//    static Pattern p = Pattern.compile("(\\d)+");
    static Pattern p = Pattern.compile("(\\d)+|(\\d)+(\\.)(\\d)");

    public Query parseQuery(String query) throws QuerySyntaxException {
        query = query.trim();
        Query q = new Query();
        if (query.indexOf("select") == 0 || query.indexOf("SELECT") == 0) {
            q.setType(QueryType.select);
        }
        // todo other types.

        int whereIndex = query.indexOf("WHERE");
        int outputIndex = query.indexOf("OUTPUT");
        int itemp = query.indexOf("OUTPUT AS");
        if (itemp > outputIndex) {
            outputIndex = itemp;
        }
        int retainIndex = query.indexOf("RETAIN");


        String inputs = null;
        if (whereIndex < 1) {
            if (retainIndex < 1) {
                inputs = query.substring(7, outputIndex);

            } else {
                inputs = query.substring(7, retainIndex);

            }
        } else {
            inputs = query.substring(7, whereIndex);

        }
        String wherePart = null;
        int tempIndex = outputIndex;
        if (retainIndex > 0 && retainIndex < outputIndex) {
            tempIndex = retainIndex;
        }
        if (whereIndex > 0) {
            wherePart = query.substring(whereIndex + 6, tempIndex);
        }
        String output = query.substring(outputIndex + 10, query.length());

        String[] parts = inputs.trim().split(",");
        String[] temp = null;
        String[] in = new String[parts.length];
        int i = 0;
        String title = null;

        int[] ops = new int[parts.length];
        for (int z = 0; z < parts.length; z++) {
            parts[z] = parts[z].trim();
        }
        q.setResultHeaders(parts);
//        System.out.println(Arrays.toString(parts));
        for (String item : parts) {
            item = item.trim();
            if (!item.endsWith(")")) {
                temp = item.split("\\.");
                in[i] = temp[1].trim();
                ops[i] = Query.pass;

                if (title == null) {
                    title = temp[0].trim();
                }
                i++;
            } else {
                int ind = item.indexOf("(");
                String oper = item.substring(0, ind);

                Integer opint = Query.operatorMap.get(oper.trim());
                if (opint != null) {
                    ops[i] = opint;
                } else {
                    ops[i] = Query.pass;
                }

                temp = item.substring(ind + 1, item.length() - 1).split("\\.");
                in[i] = temp[1].trim();
                if (title == null) {
                    title = temp[0].trim();
                }
                i++;


            }

        }


        q.setInputTitle(title);
        q.setInputs(in);
        q.setOperations(ops);

        String outTitle = output.trim();
        q.setOutputTitle(outTitle);
//        System.out.println(Arrays.toString(q.getResultHeaders()));

        // todo expand this to support composite predicates..
        String delim = null;

        if (wherePart != null) {
            for (String it : operators) {
                if (wherePart.contains(it)) {
                    delim = it;
                    break;
                }
            }
        }

        String[][] conditions = new String[1][4];
        if (wherePart != null) {
            temp = wherePart.split(delim);
            String[] temp2 = null;
            temp2 = temp[0].trim().split("\\.");
            conditions[0][0] = temp2[1].trim();
            conditions[0][1] = delim;
            Matcher m = p.matcher(temp[1]);
            temp[1] = temp[1].trim();
            if ((!temp[1].contains("'")) && (!m.find()) && (temp[1].contains("."))) {
                conditions[0][2] = temp[1].split("\\.")[1].trim();

            } else {
                conditions[0][2] = temp[1];
            }
        }
        q.setConditions(conditions);

        return q;
    }
}
