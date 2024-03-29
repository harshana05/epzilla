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
package org.epzilla.client.xml;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;


/**
 * net.epzilla.accumulator.util.XMLElement is a representation of an XML object. The object is able to parse
 * XML code.
 * <P><DL>
 * <DT><B>Parsing XML Data</B></DT>
 * <DD>
 * You can parse XML data using the following code:
 * <UL><CODE>
 * net.epzilla.accumulator.util.XMLElement xml = new net.epzilla.accumulator.util.XMLElement();<BR>
 * FileReader reader = new FileReader("filename.xml");<BR>
 * xml.parseFromReader(reader);
 * </CODE></UL></DD></DL>
 * <DL><DT><B>Retrieving Attributes</B></DT>
 * <DD>
 * You can enumerate the attributes of an element using the method
 * {@link #enumerateAttributeNames() enumerateAttributeNames}.
 * The attribute values can be retrieved using the method
 * {@link #getStringAttribute(java.lang.String) getStringAttribute}.
 * The following example shows how to list the attributes of an element:
 * <UL><CODE>
 * net.epzilla.accumulator.util.XMLElement element = ...;<BR>
 * Enumeration enum = element.getAttributeNames();<BR>
 * while (enum.hasMoreElements()) {<BR>
 * &nbsp;&nbsp;&nbsp;&nbsp;String key = (String) enum.nextElement();<BR>
 * &nbsp;&nbsp;&nbsp;&nbsp;String value = element.getStringAttribute(key);<BR>
 * &nbsp;&nbsp;&nbsp;&nbsp;System.out.println(key + " = " + value);<BR>
 * }
 * </CODE></UL></DD></DL>
 * <DL><DT><B>Retrieving Child Elements</B></DT>
 * <DD>
 * You can enumerate the children of an element using
 * {@link #enumerateChildren() enumerateChildren}.
 * The number of child elements can be retrieved using
 * {@link #countChildren() countChildren}.
 * </DD></DL>
 * <DL><DT><B>Elements Containing Character Data</B></DT>
 * <DD>
 * If an elements contains character data, like in the following example:
 * <UL><CODE>
 * &lt;title&gt;The Title&lt;/title&gt;
 * </CODE></UL>
 * you can retrieve that data using the method
 * {@link #getContent() getContent}.
 * </DD></DL>
 * <DL><DT><B>Subclassing net.epzilla.accumulator.util.XMLElement</B></DT>
 * <DD>
 * When subclassing net.epzilla.accumulator.util.XMLElement, you need to override the method
 * {@link #createAnotherElement() createAnotherElement}
 * which has to return a new copy of the receiver.
 * </DD></DL>
 * <p/>
 *
 * @author Marc De Scheemaecker
 *         &lt;<A href="mailto:cyberelf@mac.com">cyberelf@mac.com</A>&gt;
 * @version $Name:  $, $Revision: 1.1.1.1 $
 */
public class XMLElement {

    /**
     * Serialization serial version ID.
     */
    static final long serialVersionUID = 6685035139346394777L;


    /**
     * Major version of NanoXML. Classes with the same major and minor
     * version are binary compatible. Classes with the same major version
     * are source compatible. If the major version is different, you may
     * need to modify the client source code.
     *
     * @see XMLElement#NANOXML_MINOR_VERSION
     */
    //public static final int NANOXML_MAJOR_VERSION = 2;


    /**
     * Minor version of NanoXML. Classes with the same major and minor
     * version are binary compatible. Classes with the same major version
     * are source compatible. If the major version is different, you may
     * need to modify the client source code.
     *
     * @see XMLElement#NANOXML_MAJOR_VERSION
     */
    // public static final int NANOXML_MINOR_VERSION = 2;


    /**
     * The attributes given to the element.
     * <p/>
     * <dl><dt><b>Invariants:</b></dt><dd>
     * <ul><li>The field can be empty.
     * <li>The field is never <code>null</code>.
     * <li>The keys and the values are strings.
     * </ul></dd></dl>
     */
    private Hashtable<String, String> attributes;


    /**
     * Child elements of the element.
     * <p/>
     * <dl><dt><b>Invariants:</b></dt><dd>
     * <ul><li>The field can be empty.
     * <li>The field is never <code>null</code>.
     * <li>The elements are instances of <code>net.epzilla.accumulator.util.XMLElement</code>
     * or a subclass of <code>net.epzilla.accumulator.util.XMLElement</code>.
     * </ul></dd></dl>
     */
    private ArrayList<XMLElement> children;


    /**
     * The name of the element.
     * <p/>
     * <dl><dt><b>Invariants:</b></dt><dd>
     * <ul><li>The field is <code>null</code> iff the element is not
     * initialized by either parse or setName.
     * <li>If the field is not <code>null</code>, it's not empty.
     * <li>If the field is not <code>null</code>, it contains a valid
     * XML identifier.
     * </ul></dd></dl>
     */
    private String name;


    /**
     * The #PCDATA content of the object.
     * <p/>
     * <dl><dt><b>Invariants:</b></dt><dd>
     * <ul><li>The field is <code>null</code> iff the element is not a
     * #PCDATA element.
     * <li>The field can be any string, including the empty string.
     * </ul></dd></dl>
     */
    private String contents;


    /**
     * Conversion table for &amp;...; entities. The keys are the entity names
     * without the &amp; and ; delimiters.
     * <p/>
     * <dl><dt><b>Invariants:</b></dt><dd>
     * <ul><li>The field is never <code>null</code>.
     * <li>The field always contains the following associations:
     * "lt"&nbsp;=&gt;&nbsp;"&lt;", "gt"&nbsp;=&gt;&nbsp;"&gt;",
     * "quot"&nbsp;=&gt;&nbsp;"\"", "apos"&nbsp;=&gt;&nbsp;"'",
     * "amp"&nbsp;=&gt;&nbsp;"&amp;"
     * <li>The keys are strings
     * <li>The values are char arrays
     * </ul></dd></dl>
     */
    private Hashtable entities;


    /**
     * The line number where the element starts.
     * <p/>
     * <dl><dt><b>Invariants:</b></dt><dd>
     * <ul><li><code>lineNr &gt= 0</code>
     * </ul></dd></dl>
     */
    private int lineNr;


    /**
     * <code>true</code> if the case of the element and attribute names
     * are case insensitive.
     */
    private boolean ignoreCase = true;


    /**
     * <code>true</code> if the leading and trailing whitespace of #PCDATA
     * sections have to be ignored.
     */
    private boolean ignoreWhitespace;


    /**
     * Character read too much.
     * This character provides push-back functionality to the input reader
     * without having to use a PushbackReader.
     * If there is no such character, this field is '\0'.
     */
    private char charReadTooMuch;


    /**
     * The reader provided by the caller of the parse method.
     * <p/>
     * <dl><dt><b>Invariants:</b></dt><dd>
     * <ul><li>The field is not <code>null</code> while the parse method
     * is running.
     * </ul></dd></dl>
     */
    private char[] reader;

    private int charIndex;


    /**
     * The current line number in the source content.
     * <p/>
     * <dl><dt><b>Invariants:</b></dt><dd>
     * <ul><li>parserLineNr &gt; 0 while the parse method is running.
     * </ul></dd></dl>
     */
    private int parserLineNr;


    /**
     * Creates and initializes a new XML element.
     * Calling the construction is equivalent to:
     * <ul><code>new net.epzilla.accumulator.util.XMLElement(new Hashtable(), false, true)
     * </code></ul>
     * <p/>
     * <dl><dt><b>Postconditions:</b></dt><dd>
     * <ul><li>countChildren() => 0
     * <li>enumerateChildren() => empty enumeration
     * <li>enumeratePropertyNames() => empty enumeration
     * <li>getChildren() => empty vector
     * <li>getContent() => ""
     * <li>getLineNr() => 0
     * <li>getName() => null
     * </ul></dd></dl>
     *
     * @see XMLElement#XMLElement(java.util.Hashtable)
     *      net.epzilla.accumulator.util.XMLElement(Hashtable)
     * @see XMLElement#XMLElement(boolean)
     * @see XMLElement#XMLElement(java.util.Hashtable,boolean)
     *      net.epzilla.accumulator.util.XMLElement(Hashtable, boolean)
     */
    public XMLElement() {
        this(new Hashtable(), false, true, true);
    }


    /**
     * Creates and initializes a new XML element.
     * Calling the construction is equivalent to:
     * <ul><code>new net.epzilla.accumulator.util.XMLElement(entities, false, true)
     * </code></ul>
     *
     * @param entities The entity conversion table.
     *                 <p/>
     *                 </dl><dl><dt><b>Preconditions:</b></dt><dd>
     *                 <ul><li><code>entities != null</code>
     *                 </ul></dd></dl>
     *                 <p/>
     *                 <dl><dt><b>Postconditions:</b></dt><dd>
     *                 <ul><li>countChildren() => 0
     *                 <li>enumerateChildren() => empty enumeration
     *                 <li>enumeratePropertyNames() => empty enumeration
     *                 <li>getChildren() => empty vector
     *                 <li>getContent() => ""
     *                 <li>getLineNr() => 0
     *                 <li>getName() => null
     *                 </ul></dd></dl><dl>
     * @see XMLElement#XMLElement()
     * @see XMLElement#XMLElement(boolean)
     * @see XMLElement#XMLElement(java.util.Hashtable,boolean)
     *      net.epzilla.accumulator.util.XMLElement(Hashtable, boolean)
     */
    public XMLElement(Hashtable entities) {
        this(entities, false, true, true);
    }


    /**
     * Creates and initializes a new XML element.
     * Calling the construction is equivalent to:
     * <ul><code>new net.epzilla.accumulator.util.XMLElement(new Hashtable(), skipLeadingWhitespace, true)
     * </code></ul>
     *
     * @param skipLeadingWhitespace <code>true</code> if leading and trailing whitespace in PCDATA
     *                              content has to be removed.
     *                              <p/>
     *                              </dl><dl><dt><b>Postconditions:</b></dt><dd>
     *                              <ul><li>countChildren() => 0
     *                              <li>enumerateChildren() => empty enumeration
     *                              <li>enumeratePropertyNames() => empty enumeration
     *                              <li>getChildren() => empty vector
     *                              <li>getContent() => ""
     *                              <li>getLineNr() => 0
     *                              <li>getName() => null
     *                              </ul></dd></dl><dl>
     *                              <p/>
     *                              net.epzilla.accumulator.util.XMLElement#net.epzilla.accumulator.util.XMLElement()
     *                              net.epzilla.accumulator.util.XMLElement#net.epzilla.accumulator.util.XMLElement(java.util.Hashtable)
     *                              net.epzilla.accumulator.util.XMLElement(Hashtable)
     *                              net.epzilla.accumulator.util.XMLElement#net.epzilla.accumulator.util.XMLElement(java.util.Hashtable,boolean)
     *                              net.epzilla.accumulator.util.XMLElement(Hashtable, boolean)
     */
    public XMLElement(boolean skipLeadingWhitespace) {
        this(new Hashtable(), skipLeadingWhitespace, true, true);
    }


    /**
     * Creates and initializes a new XML element.
     * Calling the construction is equivalent to:
     * <ul><code>new net.epzilla.accumulator.util.XMLElement(entities, skipLeadingWhitespace, true)
     * </code></ul>
     *
     * @param entities              The entity conversion table.
     * @param skipLeadingWhitespace <code>true</code> if leading and trailing whitespace in PCDATA
     *                              content has to be removed.
     *                              <p/>
     *                              </dl><dl><dt><b>Preconditions:</b></dt><dd>
     *                              <ul><li><code>entities != null</code>
     *                              </ul></dd></dl>
     *                              <p/>
     *                              <dl><dt><b>Postconditions:</b></dt><dd>
     *                              <ul><li>countChildren() => 0
     *                              <li>enumerateChildren() => empty enumeration
     *                              <li>enumeratePropertyNames() => empty enumeration
     *                              <li>getChildren() => empty vector
     *                              <li>getContent() => ""
     *                              <li>getLineNr() => 0
     *                              <li>getName() => null
     *                              </ul></dd></dl><dl>
     *                              <p/>
     *                              net.epzilla.accumulator.util.XMLElement#net.epzilla.accumulator.util.XMLElement()
     *                              net.epzilla.accumulator.util.XMLElement#net.epzilla.accumulator.util.XMLElement(boolean)
     *                              net.epzilla.accumulator.util.XMLElement#net.epzilla.accumulator.util.XMLElement(java.util.Hashtable)
     *                              net.epzilla.accumulator.util.XMLElement(Hashtable)
     */
    public XMLElement(Hashtable entities,
                      boolean skipLeadingWhitespace) {
        this(entities, skipLeadingWhitespace, true, true);
    }


    /**
     * Creates and initializes a new XML element.
     *
     * @param entities              The entity conversion table.
     * @param skipLeadingWhitespace <code>true</code> if leading and trailing whitespace in PCDATA
     *                              content has to be removed.
     * @param ignoreCase            <code>true</code> if the case of element and attribute names have
     *                              to be ignored.
     *                              <p/>
     *                              </dl><dl><dt><b>Preconditions:</b></dt><dd>
     *                              <ul><li><code>entities != null</code>
     *                              </ul></dd></dl>
     *                              <p/>
     *                              <dl><dt><b>Postconditions:</b></dt><dd>
     *                              <ul><li>countChildren() => 0
     *                              <li>enumerateChildren() => empty enumeration
     *                              <li>enumeratePropertyNames() => empty enumeration
     *                              <li>getChildren() => empty vector
     *                              <li>getContent() => ""
     *                              <li>getLineNr() => 0
     *                              <li>getName() => null
     *                              </ul></dd></dl><dl>
     *                              <p/>
     *                              net.epzilla.accumulator.util.XMLElement#net.epzilla.accumulator.util.XMLElement()
     *                              net.epzilla.accumulator.util.XMLElement#net.epzilla.accumulator.util.XMLElement(boolean)
     *                              net.epzilla.accumulator.util.XMLElement#net.epzilla.accumulator.util.XMLElement(java.util.Hashtable)
     *                              net.epzilla.accumulator.util.XMLElement(Hashtable)
     *                              net.epzilla.accumulator.util.XMLElement#net.epzilla.accumulator.util.XMLElement(java.util.Hashtable,boolean)
     *                              net.epzilla.accumulator.util.XMLElement(Hashtable, boolean)
     */
    public XMLElement(Hashtable entities,
                      boolean skipLeadingWhitespace,
                      boolean ignoreCase) {
        this(entities, skipLeadingWhitespace, true, ignoreCase);
    }


    /**
     * Creates and initializes a new XML element.
     * <p/>
     * This constructor should <I>only</I> be called from
     * {@link #createAnotherElement() createAnotherElement}
     * to create child elements.
     *
     * @param entities                 The entity conversion table.
     * @param skipLeadingWhitespace    <code>true</code> if leading and trailing whitespace in PCDATA
     *                                 content has to be removed.
     * @param fillBasicConversionTable <code>true</code> if the basic entities need to be added to
     *                                 the entity list.
     * @param ignoreCase               <code>true</code> if the case of element and attribute names have
     *                                 to be ignored.
     *                                 <p/>
     *                                 </dl><dl><dt><b>Preconditions:</b></dt><dd>
     *                                 <ul><li><code>entities != null</code>
     *                                 <li>if <code>fillBasicConversionTable == false</code>
     *                                 then <code>entities</code> contains at least the following
     *                                 entries: <code>amp</code>, <code>lt</code>, <code>gt</code>,
     *                                 <code>apos</code> and <code>quot</code>
     *                                 </ul></dd></dl>
     *                                 <p/>
     *                                 <dl><dt><b>Postconditions:</b></dt><dd>
     *                                 <ul><li>countChildren() => 0
     *                                 <li>enumerateChildren() => empty enumeration
     *                                 <li>enumeratePropertyNames() => empty enumeration
     *                                 <li>getChildren() => empty vector
     *                                 <li>getContent() => ""
     *                                 <li>getLineNr() => 0
     *                                 <li>getName() => null
     *                                 </ul></dd></dl><dl>
     *                                 <p/>
     *                                 net.epzilla.accumulator.util.XMLElement#createAnotherElement()
     */
    protected XMLElement(Hashtable entities,
                         boolean skipLeadingWhitespace,
                         boolean fillBasicConversionTable,
                         boolean ignoreCase) {
        this.ignoreWhitespace = skipLeadingWhitespace;
        this.ignoreCase = ignoreCase;
        this.name = null;
        this.contents = "";
        this.attributes = new Hashtable<String, String>();
        this.children = new ArrayList<XMLElement>();
        this.entities = entities;
        this.lineNr = 0;
        Enumeration enume = this.entities.keys();
        while (enume.hasMoreElements()) {
            Object key = enume.nextElement();
            Object value = this.entities.get(key);
            if (value instanceof String) {
                value = ((String) value).toCharArray();
                this.entities.put(key, value);
            }
        }
        if (fillBasicConversionTable) {
            this.entities.put("amp", new char[]{'&'});
            this.entities.put("quot", new char[]{'"'});
            this.entities.put("apos", new char[]{'\''});
            this.entities.put("lt", new char[]{'<'});
            this.entities.put("gt", new char[]{'>'});
        }
    }


    /**
     * Adds a child element.
     *
     * @param child The child element to add.
     *              <p/>
     *              </dl><dl><dt><b>Preconditions:</b></dt><dd>
     *              <ul><li><code>child != null</code>
     *              <li><code>child.getName() != null</code>
     *              <li><code>child</code> does not have a parent element
     *              </ul></dd></dl>
     *              <p/>
     *              <dl><dt><b>Postconditions:</b></dt><dd>
     *              <ul><li>countChildren() => old.countChildren() + 1
     *              <li>enumerateChildren() => old.enumerateChildren() + child
     *              <li>getChildren() => old.enumerateChildren() + child
     *              </ul></dd></dl><dl>
     *              <p/>
     *              net.epzilla.accumulator.util.XMLElement#countChildren()
     *              net.epzilla.accumulator.util.XMLElement#enumerateChildren()
     *              net.epzilla.accumulator.util.XMLElement#getChildren()
     *              net.epzilla.accumulator.util.XMLElement#removeChild(nanoxml.net.epzilla.accumulator.util.XMLElement)
     *              removeChild(net.epzilla.accumulator.util.XMLElement)
     */
    public void addChild(XMLElement child) {
        this.children.add(child);
    }


    /**
     * Adds or modifies an attribute.
     *
     * @param name  The name of the attribute.
     * @param value The value of the attribute.
     *              <p/>
     *              </dl><dl><dt><b>Preconditions:</b></dt><dd>
     *              <ul><li><code>name != null</code>
     *              <li><code>name</code> is a valid XML identifier
     *              <li><code>value != null</code>
     *              </ul></dd></dl>
     *              <p/>
     *              <dl><dt><b>Postconditions:</b></dt><dd>
     *              <ul><li>enumerateAttributeNames()
     *              => old.enumerateAttributeNames() + name
     *              <li>getAttribute(name) => value
     *              </ul></dd></dl><dl>
     *              <p/>
     *              net.epzilla.accumulator.util.XMLElement#setDoubleAttribute(java.lang.String, double)
     *              setDoubleAttribute(String, double)
     *              net.epzilla.accumulator.util.XMLElement#setIntAttribute(java.lang.String, int)
     *              setIntAttribute(String, int)
     *              net.epzilla.accumulator.util.XMLElement#enumerateAttributeNames()
     *              net.epzilla.accumulator.util.XMLElement#getAttribute(java.lang.String)
     *              getAttribute(String)
     *              net.epzilla.accumulator.util.XMLElement#getAttribute(java.lang.String, java.lang.Object)
     *              getAttribute(String, Object)
     *              net.epzilla.accumulator.util.XMLElement#getAttribute(java.lang.String,
     *              java.util.Hashtable,
     *              java.lang.String, boolean)
     *              getAttribute(String, Hashtable, String, boolean)
     *              net.epzilla.accumulator.util.XMLElement#getStringAttribute(java.lang.String)
     *              getStringAttribute(String)
     *              net.epzilla.accumulator.util.XMLElement#getStringAttribute(java.lang.String,
     *              java.lang.String)
     *              getStringAttribute(String, String)
     *              net.epzilla.accumulator.util.XMLElement#getStringAttribute(java.lang.String,
     *              java.util.Hashtable,
     *              java.lang.String, boolean)
     *              getStringAttribute(String, Hashtable, String, boolean)
     */
    public void setAttribute(String name,
                             Object value) {
        if (this.ignoreCase) {
            name = name.toUpperCase();
        }
        this.attributes.put(name, value.toString());
    }


    /**
     * Adds or modifies an attribute.
     *
     * @param name  The name of the attribute.
     * @param value The value of the attribute.
     * @deprecated Use {@link #setAttribute(java.lang.String, java.lang.Object)
     *             setAttribute} instead.
     */
    public void addProperty(String name,
                            Object value) {
        this.setAttribute(name, value);
    }


    /**
     * Adds or modifies an attribute.
     *
     * @param name
     *     The name of the attribute.
     * @param value
     *     The value of the attribute.
     *
     * </dl><dl><dt><b>Preconditions:</b></dt><dd>
     * <ul><li><code>name != null</code>
     *     <li><code>name</code> is a valid XML identifier
     * </ul></dd></dl>
     *
     * <dl><dt><b>Postconditions:</b></dt><dd>
     * <ul><li>enumerateAttributeNames()
     *         => old.enumerateAttributeNames() + name
     *     <li>getIntAttribute(name) => value
     * </ul></dd></dl><dl>
     *
     *  net.epzilla.accumulator.util.XMLElement#setDoubleAttribute(java.lang.String, double)
     *         setDoubleAttribute(String, double)
     *  net.epzilla.accumulator.util.XMLElement#setAttribute(java.lang.String, java.lang.Object)
     *         setAttribute(String, Object)
     *  net.epzilla.accumulator.util.XMLElement#removeAttribute(java.lang.String)
     *         removeAttribute(String)
     *  net.epzilla.accumulator.util.XMLElement#enumerateAttributeNames()
     *  net.epzilla.accumulator.util.XMLElement#getIntAttribute(java.lang.String)
     *         getIntAttribute(String)
     *  net.epzilla.accumulator.util.XMLElement#getIntAttribute(java.lang.String, int)
     *         getIntAttribute(String, int)
     *  net.epzilla.accumulator.util.XMLElement#getIntAttribute(java.lang.String,
     *                                         java.util.Hashtable,
     *                                         java.lang.String, boolean)
     *         getIntAttribute(String, Hashtable, String, boolean)
     */
//    public void setIntAttribute(String name,
//                                int    value)
//    {
//        if (this.ignoreCase) {
//            name = name.toUpperCase();
//        }
//        this.attributes.put(name, Integer.toString(value));
//    }


    /**
     * Adds or modifies an attribute.
     *
     * @param name
     *     The name of the attribute.
     * @param value
     *     The value of the attribute.
     *
     * @deprecated Use {@link #setIntAttribute(java.lang.String, int)
     *             setIntAttribute} instead.
     */
//    public void addProperty(String key,
//                            int    value)
//    {
//        this.setIntAttribute(key, value);
//    }


    /**
     * Adds or modifies an attribute.
     *
     * @param name
     *     The name of the attribute.
     * @param value
     *     The value of the attribute.
     *
     * </dl><dl><dt><b>Preconditions:</b></dt><dd>
     * <ul><li><code>name != null</code>
     *     <li><code>name</code> is a valid XML identifier
     * </ul></dd></dl>
     *
     * <dl><dt><b>Postconditions:</b></dt><dd>
     * <ul><li>enumerateAttributeNames()
     *         => old.enumerateAttributeNames() + name
     *     <li>getDoubleAttribute(name) => value
     * </ul></dd></dl><dl>
     *
     *  net.epzilla.accumulator.util.XMLElement#setIntAttribute(java.lang.String, int)
     *         setIntAttribute(String, int)
     *  net.epzilla.accumulator.util.XMLElement#setAttribute(java.lang.String, java.lang.Object)
     *         setAttribute(String, Object)
     *  net.epzilla.accumulator.util.XMLElement#removeAttribute(java.lang.String)
     *         removeAttribute(String)
     *  net.epzilla.accumulator.util.XMLElement#enumerateAttributeNames()
     *  net.epzilla.accumulator.util.XMLElement#getDoubleAttribute(java.lang.String)
     *         getDoubleAttribute(String)
     *  net.epzilla.accumulator.util.XMLElement#getDoubleAttribute(java.lang.String, double)
     *         getDoubleAttribute(String, double)
     *  net.epzilla.accumulator.util.XMLElement#getDoubleAttribute(java.lang.String,
     *                                            java.util.Hashtable,
     *                                            java.lang.String, boolean)
     *         getDoubleAttribute(String, Hashtable, String, boolean)
     */
//    public void setDoubleAttribute(String name,
//                                   double value)
//    {
//        if (this.ignoreCase) {
//            name = name.toUpperCase();
//        }
//        this.attributes.put(name, Double.toString(value));
//    }


    /**
     * Adds or modifies an attribute.
     *
     * @param name
     *     The name of the attribute.
     * @param value
     *     The value of the attribute.
     *
     * @deprecated Use {@link #setDoubleAttribute(java.lang.String, double)
     *             setDoubleAttribute} instead.
     */
//    public void addProperty(String name,
//                            double value)
//    {
//        this.setDoubleAttribute(name, value);
//    }


    /**
     * Returns the number of child elements of the element.
     * <p/>
     * <dl><dt><b>Postconditions:</b></dt><dd>
     * <ul><li><code>result >= 0</code>
     * </ul></dd></dl>
     * <p/>
     * net.epzilla.accumulator.util.XMLElement#addChild(nanoxml.net.epzilla.accumulator.util.XMLElement)
     * addChild(net.epzilla.accumulator.util.XMLElement)
     * net.epzilla.accumulator.util.XMLElement#enumerateChildren()
     * net.epzilla.accumulator.util.XMLElement#getChildren()
     * net.epzilla.accumulator.util.XMLElement#removeChild(nanoxml.net.epzilla.accumulator.util.XMLElement)
     * removeChild(net.epzilla.accumulator.util.XMLElement)
     */
    public int countChildren() {
        return this.children.size();
    }


    /**
     * Enumerates the attribute names.
     * <p/>
     * <dl><dt><b>Postconditions:</b></dt><dd>
     * <ul><li><code>result != null</code>
     * </ul></dd></dl>
     * <p/>
     * net.epzilla.accumulator.util.XMLElement#setDoubleAttribute(java.lang.String, double)
     * setDoubleAttribute(String, double)
     * net.epzilla.accumulator.util.XMLElement#setIntAttribute(java.lang.String, int)
     * setIntAttribute(String, int)
     * net.epzilla.accumulator.util.XMLElement#setAttribute(java.lang.String, java.lang.Object)
     * setAttribute(String, Object)
     * net.epzilla.accumulator.util.XMLElement#removeAttribute(java.lang.String)
     * removeAttribute(String)
     * net.epzilla.accumulator.util.XMLElement#getAttribute(java.lang.String)
     * getAttribute(String)
     * net.epzilla.accumulator.util.XMLElement#getAttribute(java.lang.String, java.lang.Object)
     * getAttribute(String, String)
     * net.epzilla.accumulator.util.XMLElement#getAttribute(java.lang.String,
     * java.util.Hashtable,
     * java.lang.String, boolean)
     * getAttribute(String, Hashtable, String, boolean)
     * net.epzilla.accumulator.util.XMLElement#getStringAttribute(java.lang.String)
     * getStringAttribute(String)
     * net.epzilla.accumulator.util.XMLElement#getStringAttribute(java.lang.String,
     * java.lang.String)
     * getStringAttribute(String, String)
     * net.epzilla.accumulator.util.XMLElement#getStringAttribute(java.lang.String,
     * java.util.Hashtable,
     * java.lang.String, boolean)
     * getStringAttribute(String, Hashtable, String, boolean)
     * net.epzilla.accumulator.util.XMLElement#getIntAttribute(java.lang.String)
     * getIntAttribute(String)
     * net.epzilla.accumulator.util.XMLElement#getIntAttribute(java.lang.String, int)
     * getIntAttribute(String, int)
     * net.epzilla.accumulator.util.XMLElement#getIntAttribute(java.lang.String,
     * java.util.Hashtable,
     * java.lang.String, boolean)
     * getIntAttribute(String, Hashtable, String, boolean)
     * net.epzilla.accumulator.util.XMLElement#getDoubleAttribute(java.lang.String)
     * getDoubleAttribute(String)
     * net.epzilla.accumulator.util.XMLElement#getDoubleAttribute(java.lang.String, double)
     * getDoubleAttribute(String, double)
     * net.epzilla.accumulator.util.XMLElement#getDoubleAttribute(java.lang.String,
     * java.util.Hashtable,
     * java.lang.String, boolean)
     * getDoubleAttribute(String, Hashtable, String, boolean)
     * net.epzilla.accumulator.util.XMLElement#getBooleanAttribute(java.lang.String,
     * java.lang.String,
     * java.lang.String, boolean)
     * getBooleanAttribute(String, String, String, boolean)
     */
    public Enumeration enumerateAttributeNames() {
        return this.attributes.keys();
    }


    /**
     * Enumerates the attribute names.
     *
     * @deprecated Use {@link #enumerateAttributeNames()
     *             enumerateAttributeNames} instead.
     */
    public Enumeration enumeratePropertyNames() {
        return this.enumerateAttributeNames();
    }


    /**
     * Enumerates the child elements.
     * <p/>
     * <dl><dt><b>Postconditions:</b></dt><dd>
     * <ul><li><code>result != null</code>
     * </ul></dd></dl>
     * <p/>
     * net.epzilla.accumulator.util.XMLElement#addChild(nanoxml.net.epzilla.accumulator.util.XMLElement)
     * addChild(net.epzilla.accumulator.util.XMLElement)
     * net.epzilla.accumulator.util.XMLElement#countChildren()
     * net.epzilla.accumulator.util.XMLElement#getChildren()
     * net.epzilla.accumulator.util.XMLElement#removeChild(nanoxml.net.epzilla.accumulator.util.XMLElement)
     * removeChild(net.epzilla.accumulator.util.XMLElement)
     */
    public Iterator<XMLElement> enumerateChildren() {
        return this.children.iterator();
    }


    /**
     * Returns the child elements as a Vector. It is safe to modify this
     * Vector.
     * <p/>
     * <dl><dt><b>Postconditions:</b></dt><dd>
     * <ul><li><code>result != null</code>
     * </ul></dd></dl>
     * <p/>
     * net.epzilla.accumulator.util.XMLElement#addChild(nanoxml.net.epzilla.accumulator.util.XMLElement)
     * addChild(net.epzilla.accumulator.util.XMLElement)
     * net.epzilla.accumulator.util.XMLElement#countChildren()
     * net.epzilla.accumulator.util.XMLElement#enumerateChildren()
     * net.epzilla.accumulator.util.XMLElement#removeChild(nanoxml.net.epzilla.accumulator.util.XMLElement)
     * removeChild(net.epzilla.accumulator.util.XMLElement)
     */
    public ArrayList<XMLElement> getChildren() {
        try {
            return this.children;
        } catch (Exception e) {
            // this never happens, however, some Java compilers are so
            // braindead that they require this exception clause
            return null;
        }
    }

    public XMLElement getRoot() {
        try {
            return this.children.get(0);
        } catch (Exception e) {
            // this never happens, however, some Java compilers are so
            // braindead that they require this exception clause
            return null;
        }
    }

    /**
     * Returns the PCDATA content of the object. If there is no such content,
     * <CODE>null</CODE> is returned.
     *
     * @deprecated Use {@link #getContent() getContent} instead.
     */
    public String getContents() {
        return this.getContent();
    }


    /**
     * Returns the PCDATA content of the object. If there is no such content,
     * <CODE>null</CODE> is returned.
     * <p/>
     * net.epzilla.accumulator.util.XMLElement#setContent(java.lang.String)
     * setContent(String)
     */
    public String getContent() {
        return this.contents;
    }


    /**
     * Returns the line nr in the source data on which the element is found.
     * This method returns <code>0</code> there is no associated source data.
     * <p/>
     * <dl><dt><b>Postconditions:</b></dt><dd>
     * <ul><li><code>result >= 0</code>
     * </ul></dd></dl>
     */
    public int getLineNr() {
        return this.lineNr;
    }


    /**
     * Returns an attribute of the element.
     * If the attribute doesn't exist, <code>null</code> is returned.
     *
     * @param name The name of the attribute.
     *             <p/>
     *             </dl><dl><dt><b>Preconditions:</b></dt><dd>
     *             <ul><li><code>name != null</code>
     *             <li><code>name</code> is a valid XML identifier
     *             </ul></dd></dl><dl>
     *             <p/>
     *             net.epzilla.accumulator.util.XMLElement#setAttribute(java.lang.String, java.lang.Object)
     *             setAttribute(String, Object)
     *             net.epzilla.accumulator.util.XMLElement#removeAttribute(java.lang.String)
     *             removeAttribute(String)
     *             net.epzilla.accumulator.util.XMLElement#enumerateAttributeNames()
     *             net.epzilla.accumulator.util.XMLElement#getAttribute(java.lang.String, java.lang.Object)
     *             getAttribute(String, Object)
     *             net.epzilla.accumulator.util.XMLElement#getAttribute(java.lang.String,
     *             java.util.Hashtable,
     *             java.lang.String, boolean)
     *             getAttribute(String, Hashtable, String, boolean)
     */
    public String getAttribute(String name) {
        return this.getAttribute(name, null);
    }


    /**
     * Returns an attribute of the element.
     * If the attribute doesn't exist, <code>defaultValue</code> is returned.
     *
     * @param name         The name of the attribute.
     * @param defaultValue Key to use if the attribute is missing.
     *                     <p/>
     *                     </dl><dl><dt><b>Preconditions:</b></dt><dd>
     *                     <ul><li><code>name != null</code>
     *                     <li><code>name</code> is a valid XML identifier
     *                     </ul></dd></dl><dl>
     *                     <p/>
     *                     net.epzilla.accumulator.util.XMLElement#setAttribute(java.lang.String, java.lang.Object)
     *                     setAttribute(String, Object)
     *                     net.epzilla.accumulator.util.XMLElement#removeAttribute(java.lang.String)
     *                     removeAttribute(String)
     *                     net.epzilla.accumulator.util.XMLElement#enumerateAttributeNames()
     *                     net.epzilla.accumulator.util.XMLElement#getAttribute(java.lang.String)
     *                     getAttribute(String)
     *                     net.epzilla.accumulator.util.XMLElement#getAttribute(java.lang.String,
     *                     java.util.Hashtable,
     *                     java.lang.String, boolean)
     *                     getAttribute(String, Hashtable, String, boolean)
     */
    public String getAttribute(String name,
                               String defaultValue) {
        if (this.ignoreCase) {
            name = name.toUpperCase();
        }
        String value = this.attributes.get(name);
        if (value == null) {
            value = defaultValue;
        }
        return value;
    }


    /**
     * Returns an attribute by looking up a key in a hashtable.
     * If the attribute doesn't exist, the value corresponding to defaultKey
     * is returned.
     * <p/>
     * As an example, if valueSet contains the mapping <code>"one" =>
     * "1"</code>
     * and the element contains the attribute <code>attr="one"</code>, then
     * <code>getAttribute("attr", mapping, defaultKey, false)</code> returns
     * <code>"1"</code>.
     *
     * @param name          The name of the attribute.
     * @param valueSet      Hashtable mapping keys to values.
     * @param defaultKey    Key to use if the attribute is missing.
     * @param allowLiterals <code>true</code> if literals are valid.
     *                      <p/>
     *                      </dl><dl><dt><b>Preconditions:</b></dt><dd>
     *                      <ul><li><code>name != null</code>
     *                      <li><code>name</code> is a valid XML identifier
     *                      <li><code>valueSet</code> != null
     *                      <li>the keys of <code>valueSet</code> are strings
     *                      </ul></dd></dl><dl>
     *                      <p/>
     *                      net.epzilla.accumulator.util.XMLElement#setAttribute(java.lang.String, java.lang.Object)
     *                      setAttribute(String, Object)
     *                      net.epzilla.accumulator.util.XMLElement#removeAttribute(java.lang.String)
     *                      removeAttribute(String)
     *                      net.epzilla.accumulator.util.XMLElement#enumerateAttributeNames()
     *                      net.epzilla.accumulator.util.XMLElement#getAttribute(java.lang.String)
     *                      getAttribute(String)
     *                      net.epzilla.accumulator.util.XMLElement#getAttribute(java.lang.String, java.lang.Object)
     *                      getAttribute(String, Object)
     */
    public Object getAttribute(String name,
                               Hashtable valueSet,
                               String defaultKey,
                               boolean allowLiterals) {
        if (this.ignoreCase) {
            name = name.toUpperCase();
        }
        Object key = this.attributes.get(name);
        Object result;
        if (key == null) {
            key = defaultKey;
        }
        result = valueSet.get(key);
        if (result == null) {
            if (allowLiterals) {
                result = key;
            } else {
                throw this.invalidValue(name, (String) key);
            }
        }
        return result;
    }


    /**
     * Returns an attribute of the element.
     * If the attribute doesn't exist, <code>null</code> is returned.
     *
     * @param name The name of the attribute.
     *             <p/>
     *             </dl><dl><dt><b>Preconditions:</b></dt><dd>
     *             <ul><li><code>name != null</code>
     *             <li><code>name</code> is a valid XML identifier
     *             </ul></dd></dl><dl>
     *             <p/>
     *             net.epzilla.accumulator.util.XMLElement#setAttribute(java.lang.String, java.lang.Object)
     *             setAttribute(String, Object)
     *             net.epzilla.accumulator.util.XMLElement#removeAttribute(java.lang.String)
     *             removeAttribute(String)
     *             net.epzilla.accumulator.util.XMLElement#enumerateAttributeNames()
     *             net.epzilla.accumulator.util.XMLElement#getStringAttribute(java.lang.String,
     *             java.lang.String)
     *             getStringAttribute(String, String)
     *             net.epzilla.accumulator.util.XMLElement#getStringAttribute(java.lang.String,
     *             java.util.Hashtable,
     *             java.lang.String, boolean)
     *             getStringAttribute(String, Hashtable, String, boolean)
     */
    public String getStringAttribute(String name) {
        return this.getStringAttribute(name, null);
    }


    /**
     * Returns an attribute of the element.
     * If the attribute doesn't exist, <code>defaultValue</code> is returned.
     *
     * @param name         The name of the attribute.
     * @param defaultValue Key to use if the attribute is missing.
     *                     <p/>
     *                     </dl><dl><dt><b>Preconditions:</b></dt><dd>
     *                     <ul><li><code>name != null</code>
     *                     <li><code>name</code> is a valid XML identifier
     *                     </ul></dd></dl><dl>
     *                     <p/>
     *                     net.epzilla.accumulator.util.XMLElement#setAttribute(java.lang.String, java.lang.Object)
     *                     setAttribute(String, Object)
     *                     net.epzilla.accumulator.util.XMLElement#removeAttribute(java.lang.String)
     *                     removeAttribute(String)
     *                     net.epzilla.accumulator.util.XMLElement#enumerateAttributeNames()
     *                     net.epzilla.accumulator.util.XMLElement#getStringAttribute(java.lang.String)
     *                     getStringAttribute(String)
     *                     net.epzilla.accumulator.util.XMLElement#getStringAttribute(java.lang.String,
     *                     java.util.Hashtable,
     *                     java.lang.String, boolean)
     *                     getStringAttribute(String, Hashtable, String, boolean)
     */
    public String getStringAttribute(String name,
                                     String defaultValue) {
        return (String) this.getAttribute(name, defaultValue);
    }


    /**
     * Returns an attribute by looking up a key in a hashtable.
     * If the attribute doesn't exist, the value corresponding to defaultKey
     * is returned.
     * <p/>
     * As an example, if valueSet contains the mapping <code>"one" =>
     * "1"</code>
     * and the element contains the attribute <code>attr="one"</code>, then
     * <code>getAttribute("attr", mapping, defaultKey, false)</code> returns
     * <code>"1"</code>.
     *
     * @param name          The name of the attribute.
     * @param valueSet      Hashtable mapping keys to values.
     * @param defaultKey    Key to use if the attribute is missing.
     * @param allowLiterals <code>true</code> if literals are valid.
     *                      <p/>
     *                      </dl><dl><dt><b>Preconditions:</b></dt><dd>
     *                      <ul><li><code>name != null</code>
     *                      <li><code>name</code> is a valid XML identifier
     *                      <li><code>valueSet</code> != null
     *                      <li>the keys of <code>valueSet</code> are strings
     *                      <li>the values of <code>valueSet</code> are strings
     *                      </ul></dd></dl><dl>
     *                      <p/>
     *                      net.epzilla.accumulator.util.XMLElement#setAttribute(java.lang.String, java.lang.Object)
     *                      setAttribute(String, Object)
     *                      net.epzilla.accumulator.util.XMLElement#removeAttribute(java.lang.String)
     *                      removeAttribute(String)
     *                      net.epzilla.accumulator.util.XMLElement#enumerateAttributeNames()
     *                      net.epzilla.accumulator.util.XMLElement#getStringAttribute(java.lang.String)
     *                      getStringAttribute(String)
     *                      net.epzilla.accumulator.util.XMLElement#getStringAttribute(java.lang.String,
     *                      java.lang.String)
     *                      getStringAttribute(String, String)
     */
    public String getStringAttribute(String name,
                                     Hashtable valueSet,
                                     String defaultKey,
                                     boolean allowLiterals) {
        return (String) this.getAttribute(name, valueSet, defaultKey,
                allowLiterals);
    }


    /**
     * Returns an attribute of the element.
     * If the attribute doesn't exist, <code>0</code> is returned.
     *
     * @param name The name of the attribute.
     *             <p/>
     *             </dl><dl><dt><b>Preconditions:</b></dt><dd>
     *             <ul><li><code>name != null</code>
     *             <li><code>name</code> is a valid XML identifier
     *             </ul></dd></dl><dl>
     *             <p/>
     *             net.epzilla.accumulator.util.XMLElement#setIntAttribute(java.lang.String, int)
     *             setIntAttribute(String, int)
     *             net.epzilla.accumulator.util.XMLElement#enumerateAttributeNames()
     *             net.epzilla.accumulator.util.XMLElement#getIntAttribute(java.lang.String, int)
     *             getIntAttribute(String, int)
     *             net.epzilla.accumulator.util.XMLElement#getIntAttribute(java.lang.String,
     *             java.util.Hashtable,
     *             java.lang.String, boolean)
     *             getIntAttribute(String, Hashtable, String, boolean)
     */
    public int getIntAttribute(String name) {
        return this.getIntAttribute(name, 0);
    }


    /**
     * Returns an attribute of the element.
     * If the attribute doesn't exist, <code>defaultValue</code> is returned.
     *
     * @param name         The name of the attribute.
     * @param defaultValue Key to use if the attribute is missing.
     *                     <p/>
     *                     </dl><dl><dt><b>Preconditions:</b></dt><dd>
     *                     <ul><li><code>name != null</code>
     *                     <li><code>name</code> is a valid XML identifier
     *                     </ul></dd></dl><dl>
     *                     <p/>
     *                     net.epzilla.accumulator.util.XMLElement#setIntAttribute(java.lang.String, int)
     *                     setIntAttribute(String, int)
     *                     net.epzilla.accumulator.util.XMLElement#enumerateAttributeNames()
     *                     net.epzilla.accumulator.util.XMLElement#getIntAttribute(java.lang.String)
     *                     getIntAttribute(String)
     *                     net.epzilla.accumulator.util.XMLElement#getIntAttribute(java.lang.String,
     *                     java.util.Hashtable,
     *                     java.lang.String, boolean)
     *                     getIntAttribute(String, Hashtable, String, boolean)
     */
    public int getIntAttribute(String name,
                               int defaultValue) {
        if (this.ignoreCase) {
            name = name.toUpperCase();
        }
        String value = this.attributes.get(name);
        if (value == null) {
            return defaultValue;
        } else {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                throw this.invalidValue(name, value);
            }
        }
    }


    /**
     * Returns an attribute by looking up a key in a hashtable.
     * If the attribute doesn't exist, the value corresponding to defaultKey
     * is returned.
     * <p/>
     * As an example, if valueSet contains the mapping <code>"one" => 1</code>
     * and the element contains the attribute <code>attr="one"</code>, then
     * <code>getIntAttribute("attr", mapping, defaultKey, false)</code> returns
     * <code>1</code>.
     *
     * @param name                The name of the attribute.
     * @param valueSet            Hashtable mapping keys to values.
     * @param defaultKey          Key to use if the attribute is missing.
     * @param allowLiteralNumbers <code>true</code> if literal numbers are valid.
     *                            <p/>
     *                            </dl><dl><dt><b>Preconditions:</b></dt><dd>
     *                            <ul><li><code>name != null</code>
     *                            <li><code>name</code> is a valid XML identifier
     *                            <li><code>valueSet</code> != null
     *                            <li>the keys of <code>valueSet</code> are strings
     *                            <li>the values of <code>valueSet</code> are Integer objects
     *                            <li><code>defaultKey</code> is either <code>null</code>, a
     *                            key in <code>valueSet</code> or an integer.
     *                            </ul></dd></dl><dl>
     *                            <p/>
     *                            net.epzilla.accumulator.util.XMLElement#setIntAttribute(java.lang.String, int)
     *                            setIntAttribute(String, int)
     *                            net.epzilla.accumulator.util.XMLElement#enumerateAttributeNames()
     *                            net.epzilla.accumulator.util.XMLElement#getIntAttribute(java.lang.String)
     *                            getIntAttribute(String)
     *                            net.epzilla.accumulator.util.XMLElement#getIntAttribute(java.lang.String, int)
     *                            getIntAttribute(String, int)
     */
    public int getIntAttribute(String name,
                               Hashtable valueSet,
                               String defaultKey,
                               boolean allowLiteralNumbers) {
        if (this.ignoreCase) {
            name = name.toUpperCase();
        }
        Object key = this.attributes.get(name);
        Integer result;
        if (key == null) {
            key = defaultKey;
        }
        try {
            result = (Integer) valueSet.get(key);
        } catch (ClassCastException e) {
            throw this.invalidValueSet(name);
        }
        if (result == null) {
            if (!allowLiteralNumbers) {
                throw this.invalidValue(name, (String) key);
            }
            try {
                result = Integer.valueOf((String) key);
            } catch (NumberFormatException e) {
                throw this.invalidValue(name, (String) key);
            }
        }
        return result;
    }


    /**
     * Returns an attribute of the element.
     * If the attribute doesn't exist, <code>0.0</code> is returned.
     *
     * @param name The name of the attribute.
     *
     * </dl><dl><dt><b>Preconditions:</b></dt><dd>
     * <ul><li><code>name != null</code>
     *     <li><code>name</code> is a valid XML identifier
     * </ul></dd></dl><dl>
     *
     *  net.epzilla.accumulator.util.XMLElement#setDoubleAttribute(java.lang.String, double)
     *         setDoubleAttribute(String, double)
     *  net.epzilla.accumulator.util.XMLElement#enumerateAttributeNames()
     *  net.epzilla.accumulator.util.XMLElement#getDoubleAttribute(java.lang.String, double)
     *         getDoubleAttribute(String, double)
     *  net.epzilla.accumulator.util.XMLElement#getDoubleAttribute(java.lang.String,
     *                                            java.util.Hashtable,
     *                                            java.lang.String, boolean)
     *         getDoubleAttribute(String, Hashtable, String, boolean)
     */
//    public double getDoubleAttribute(String name)
//    {
//        return this.getDoubleAttribute(name, 0.);
//    }


    /**
     * Returns an attribute of the element.
     * If the attribute doesn't exist, <code>defaultValue</code> is returned.
     *
     * @param name         The name of the attribute.
     * @param defaultValue Key to use if the attribute is missing.
     *
     * </dl><dl><dt><b>Preconditions:</b></dt><dd>
     * <ul><li><code>name != null</code>
     *     <li><code>name</code> is a valid XML identifier
     * </ul></dd></dl><dl>
     *
     *  net.epzilla.accumulator.util.XMLElement#setDoubleAttribute(java.lang.String, double)
     *         setDoubleAttribute(String, double)
     *  net.epzilla.accumulator.util.XMLElement#enumerateAttributeNames()
     *  net.epzilla.accumulator.util.XMLElement#getDoubleAttribute(java.lang.String)
     *         getDoubleAttribute(String)
     *  net.epzilla.accumulator.util.XMLElement#getDoubleAttribute(java.lang.String,
     *                                            java.util.Hashtable,
     *                                            java.lang.String, boolean)
     *         getDoubleAttribute(String, Hashtable, String, boolean)
     */
//    public double getDoubleAttribute(String name,
//                                     double defaultValue)
//    {
//        if (this.ignoreCase) {
//            name = name.toUpperCase();
//        }
//        String value = (String) this.attributes.get(name);
//        if (value == null) {
//            return defaultValue;
//        } else {
//            try {
//                return Double.valueOf(value).doubleValue();
//            } catch (NumberFormatException e) {
//                throw this.invalidValue(name, value);
//            }
//        }
//    }


    /**
     * Returns an attribute by looking up a key in a hashtable.
     * If the attribute doesn't exist, the value corresponding to defaultKey
     * is returned.
     * <P>
     * As an example, if valueSet contains the mapping <code>"one" =&gt;
     * 1.0</code>
     * and the element contains the attribute <code>attr="one"</code>, then
     * <code>getDoubleAttribute("attr", mapping, defaultKey, false)</code>
     * returns <code>1.0</code>.
     *
     * @param name
     *     The name of the attribute.
     * @param valueSet
     *     Hashtable mapping keys to values.
     * @param defaultKey
     *     Key to use if the attribute is missing.
     * @param allowLiteralNumbers
     *     <code>true</code> if literal numbers are valid.
     *
     * </dl><dl><dt><b>Preconditions:</b></dt><dd>
     * <ul><li><code>name != null</code>
     *     <li><code>name</code> is a valid XML identifier
     *     <li><code>valueSet != null</code>
     *     <li>the keys of <code>valueSet</code> are strings
     *     <li>the values of <code>valueSet</code> are Double objects
     *     <li><code>defaultKey</code> is either <code>null</code>, a
     *         key in <code>valueSet</code> or a double.
     * </ul></dd></dl><dl>
     *
     *  net.epzilla.accumulator.util.XMLElement#setDoubleAttribute(java.lang.String, double)
     *         setDoubleAttribute(String, double)
     *  net.epzilla.accumulator.util.XMLElement#enumerateAttributeNames()
     *  net.epzilla.accumulator.util.XMLElement#getDoubleAttribute(java.lang.String)
     *         getDoubleAttribute(String)
     *  net.epzilla.accumulator.util.XMLElement#getDoubleAttribute(java.lang.String, double)
     *         getDoubleAttribute(String, double)
     */
//    public double getDoubleAttribute(String    name,
//                                     Hashtable valueSet,
//                                     String    defaultKey,
//                                     boolean   allowLiteralNumbers)
//    {
//        if (this.ignoreCase) {
//            name = name.toUpperCase();
//        }
//        Object key = this.attributes.get(name);
//        Double result;
//        if (key == null) {
//            key = defaultKey;
//        }
//        try {
//            result = (Double) valueSet.get(key);
//        } catch (ClassCastException e) {
//            throw this.invalidValueSet(name);
//        }
//        if (result == null) {
//            if (! allowLiteralNumbers) {
//                throw this.invalidValue(name, (String) key);
//            }
//            try {
//                result = Double.valueOf((String) key);
//            } catch (NumberFormatException e) {
//                throw this.invalidValue(name, (String) key);
//            }
//        }
//        return result.doubleValue();
//    }


    /**
     * Returns an attribute of the element.
     * If the attribute doesn't exist, <code>defaultValue</code> is returned.
     * If the value of the attribute is equal to <code>trueValue</code>,
     * <code>true</code> is returned.
     * If the value of the attribute is equal to <code>falseValue</code>,
     * <code>false</code> is returned.
     * If the value doesn't match <code>trueValue</code> or
     * <code>falseValue</code>, an exception is thrown.
     *
     * @param name         The name of the attribute.
     * @param trueValue    The value associated with <code>true</code>.
     * @param falseValue   The value associated with <code>true</code>.
     * @param defaultValue Value to use if the attribute is missing.
     *
     * </dl><dl><dt><b>Preconditions:</b></dt><dd>
     * <ul><li><code>name != null</code>
     *     <li><code>name</code> is a valid XML identifier
     *     <li><code>trueValue</code> and <code>falseValue</code>
     *         are different strings.
     * </ul></dd></dl><dl>
     *
     *  net.epzilla.accumulator.util.XMLElement#setAttribute(java.lang.String, java.lang.Object)
     *         setAttribute(String, Object)
     *  net.epzilla.accumulator.util.XMLElement#removeAttribute(java.lang.String)
     *         removeAttribute(String)
     *  net.epzilla.accumulator.util.XMLElement#enumerateAttributeNames()
     */
//    public boolean getBooleanAttribute(String  name,
//                                       String  trueValue,
//                                       String  falseValue,
//                                       boolean defaultValue)
//    {
//        if (this.ignoreCase) {
//            name = name.toUpperCase();
//        }
//        Object value = this.attributes.get(name);
//        if (value == null) {
//            return defaultValue;
//        } else if (value.equals(trueValue)) {
//            return true;
//        } else if (value.equals(falseValue)) {
//            return false;
//        } else {
//            throw this.invalidValue(name, (String) value);
//        }
//    }


    /**
     * Returns an attribute by looking up a key in a hashtable.
     *
     * @deprecated Use {@link #getIntAttribute(java.lang.String,
     *             java.util.Hashtable, java.lang.String, boolean)
     *             getIntAttribute} instead.
     */
//    public int getIntProperty(String    name,
//                              Hashtable valueSet,
//                              String    defaultKey)
//    {
//        return this.getIntAttribute(name, valueSet, defaultKey, false);
//    }


    /**
     * Returns an attribute.
     *
     * @deprecated Use {@link #getStringAttribute(java.lang.String)
     *             getStringAttribute} instead.
     */
    public String getProperty(String name) {
        return this.getStringAttribute(name);
    }


    /**
     * Returns an attribute.
     *
     * @deprecated Use {@link #getStringAttribute(java.lang.String,
     *             java.lang.String) getStringAttribute} instead.
     */
    public String getProperty(String name,
                              String defaultValue) {
        return this.getStringAttribute(name, defaultValue);
    }


    /**
     * Returns an attribute.
     *
     * @deprecated Use {@link #getIntAttribute(java.lang.String, int)
     *             getIntAttribute} instead.
     */
    public int getProperty(String name,
                           int defaultValue) {
        return this.getIntAttribute(name, defaultValue);
    }


    /**
     * Returns an attribute.
     *
     * @deprecated Use {@link #getDoubleAttribute(java.lang.String, double)
     *             getDoubleAttribute} instead.
     */
//    public double getProperty(String name,
//                              double defaultValue)
//    {
//        return this.getDoubleAttribute(name, defaultValue);
//    }


    /**
     * Returns an attribute.
     *
     * @deprecated Use {@link #getBooleanAttribute(java.lang.String,
     *             java.lang.String, java.lang.String, boolean)
     *             getBooleanAttribute} instead.
     */
//    public boolean getProperty(String  key,
//                               String  trueValue,
//                               String  falseValue,
//                               boolean defaultValue)
//    {
//        return this.getBooleanAttribute(key, trueValue, falseValue,
//                                        defaultValue);
//    }


    /**
     * Returns an attribute by looking up a key in a hashtable.
     *
     * @deprecated Use {@link #getAttribute(java.lang.String,
     *             java.util.Hashtable, java.lang.String, boolean)
     *             getAttribute} instead.
     */
    public Object getProperty(String name,
                              Hashtable valueSet,
                              String defaultKey) {
        return this.getAttribute(name, valueSet, defaultKey, false);
    }


    /**
     * Returns an attribute by looking up a key in a hashtable.
     *
     * @deprecated Use {@link #getStringAttribute(java.lang.String,
     *             java.util.Hashtable, java.lang.String, boolean)
     *             getStringAttribute} instead.
     */
    public String getStringProperty(String name,
                                    Hashtable valueSet,
                                    String defaultKey) {
        return this.getStringAttribute(name, valueSet, defaultKey, false);
    }


    /**
     * Returns an attribute by looking up a key in a hashtable.
     *
     * @deprecated Use {@link #getIntAttribute(java.lang.String,
     *             java.util.Hashtable, java.lang.String, boolean)
     *             getIntAttribute} instead.
     */
//    public int getSpecialIntProperty(String    name,
//                                     Hashtable valueSet,
//                                     String    defaultKey)
//    {
//        return this.getIntAttribute(name, valueSet, defaultKey, true);
//    }


    /**
     * Returns an attribute by looking up a key in a hashtable.
     *
     * @deprecated Use {@link #getDoubleAttribute(java.lang.String,
     *             java.util.Hashtable, java.lang.String, boolean)
     *             getDoubleAttribute} instead.
     */
//    public double getSpecialDoubleProperty(String    name,
//                                           Hashtable valueSet,
//                                           String    defaultKey)
//    {
//        return this.getDoubleAttribute(name, valueSet, defaultKey, true);
//    }


    /**
     * Returns the name of the element.
     * <p/>
     * net.epzilla.accumulator.util.XMLElement#setName(java.lang.String) setName(String)
     */
    public String getName() {
        return this.name;
    }


    /**
     * Returns the name of the element.
     *
     * @deprecated Use {@link #getName() getName} instead.
     */
    public String getTagName() {
        return this.getName();
    }


    /**
     * Reads one XML element from a java.io.com.isi.terminal.Reader and parses it.
     *
     * @param reader The reader from which to retrieve the XML data.
     *               <p/>
     *               </dl><dl><dt><b>Preconditions:</b></dt><dd>
     *               <ul><li><code>reader != null</code>
     *               <li><code>reader</code> is not closed
     *               </ul></dd></dl>
     *               <p/>
     *               <dl><dt><b>Postconditions:</b></dt><dd>
     *               <ul><li>the state of the receiver is updated to reflect the XML element
     *               parsed from the reader
     *               <li>the reader points to the first character following the last
     *               '&gt;' character of the XML element
     *               </ul></dd></dl><dl>
     * @throws java.io.IOException If an error occured while reading the input.
     * @throws XMLParseException   If an error occured while parsing the read data.
     */
    public void parseFromChars(char[] reader)
            throws IOException, XMLParseException {
        this.parseFromChars(reader, /*startingLineNr*/ 1);
    }


    /**
     * Reads one XML element from a java.io.com.isi.terminal.Reader and parses it.
     *
     * @param reader         The reader from which to retrieve the XML data.
     * @param startingLineNr The line number of the first line in the data.
     *                       <p/>
     *                       </dl><dl><dt><b>Preconditions:</b></dt><dd>
     *                       <ul><li><code>reader != null</code>
     *                       <li><code>reader</code> is not closed
     *                       </ul></dd></dl>
     *                       <p/>
     *                       <dl><dt><b>Postconditions:</b></dt><dd>
     *                       <ul><li>the state of the receiver is updated to reflect the XML element
     *                       parsed from the reader
     *                       <li>the reader points to the first character following the last
     *                       '&gt;' character of the XML element
     *                       </ul></dd></dl><dl>
     * @throws java.io.IOException If an error occured while reading the input.
     * @throws javax.management.modelmbean.XMLParseException
     *                             If an error occured while parsing the read data.
     */
    public void parseFromChars(char[] reader,
                               int startingLineNr)
            throws IOException, XMLParseException {
        this.name = null;
        this.contents = "";
        this.attributes = new Hashtable<String, String>();
        this.children = new ArrayList<XMLElement>();
        this.charReadTooMuch = '\0';
        this.reader = reader;
        this.parserLineNr = startingLineNr;

        for (; ;) {
            char ch = this.scanWhitespace();

            if (ch != '<') {
                throw this.expectedInput("<");
            }

            ch = this.readChar();

            if ((ch == '!') || (ch == '?')) {
                this.skipSpecialTag(0);
            } else {
                this.unreadChar(ch);
                this.scanElement(this);
                return;
            }
        }
    }


    /**
     * Reads one XML element from a String and parses it.
     *
     * @param string The reader from which to retrieve the XML data.
     *               <p/>
     *               </dl><dl><dt><b>Preconditions:</b></dt><dd>
     *               <ul><li><code>string != null</code>
     *               <li><code>string.length() &gt; 0</code>
     *               </ul></dd></dl>
     *               <p/>
     *               <dl><dt><b>Postconditions:</b></dt><dd>
     *               <ul><li>the state of the receiver is updated to reflect the XML element
     *               parsed from the reader
     *               </ul></dd></dl><dl>
     * @throws XMLParseException If an error occured while parsing the string.
     */
    public void parseString(String string)
            throws XMLParseException {
        try {
            this.parseFromChars(string.toCharArray(),
                    /*startingLineNr*/ 1);
        } catch (IOException e) {
            // Java exception handling suxx
        }
    }


    /**
     * Reads one XML element from a String and parses it.
     *
     * @param string The reader from which to retrieve the XML data.
     * @param offset The first character in <code>string</code> to scan.
     *               <p/>
     *               </dl><dl><dt><b>Preconditions:</b></dt><dd>
     *               <ul><li><code>string != null</code>
     *               <li><code>offset &lt; string.length()</code>
     *               <li><code>offset &gt;= 0</code>
     *               </ul></dd></dl>
     *               <p/>
     *               <dl><dt><b>Postconditions:</b></dt><dd>
     *               <ul><li>the state of the receiver is updated to reflect the XML element
     *               parsed from the reader
     *               </ul></dd></dl><dl>
     * @throws XMLParseException If an error occured while parsing the string.
     */
    public void parseString(String string,
                            int offset)
            throws XMLParseException {
        this.parseString(string.substring(offset));
    }


    /**
     * Reads one XML element from a String and parses it.
     *
     * @param string The reader from which to retrieve the XML data.
     * @param offset The first character in <code>string</code> to scan.
     * @param end    The character where to stop scanning.
     *               This character is not scanned.
     *               <p/>
     *               </dl><dl><dt><b>Preconditions:</b></dt><dd>
     *               <ul><li><code>string != null</code>
     *               <li><code>end &lt;= string.length()</code>
     *               <li><code>offset &lt; end</code>
     *               <li><code>offset &gt;= 0</code>
     *               </ul></dd></dl>
     *               <p/>
     *               <dl><dt><b>Postconditions:</b></dt><dd>
     *               <ul><li>the state of the receiver is updated to reflect the XML element
     *               parsed from the reader
     *               </ul></dd></dl><dl>
     * @throws XMLParseException If an error occured while parsing the string.
     */
    public void parseString(String string,
                            int offset,
                            int end)
            throws XMLParseException {
        this.parseString(string.substring(offset, end));
    }


    /**
     * Reads one XML element from a String and parses it.
     *
     * @param string         The reader from which to retrieve the XML data.
     * @param offset         The first character in <code>string</code> to scan.
     * @param end            The character where to stop scanning.
     *                       This character is not scanned.
     * @param startingLineNr The line number of the first line in the data.
     *                       <p/>
     *                       </dl><dl><dt><b>Preconditions:</b></dt><dd>
     *                       <ul><li><code>string != null</code>
     *                       <li><code>end &lt;= string.length()</code>
     *                       <li><code>offset &lt; end</code>
     *                       <li><code>offset &gt;= 0</code>
     *                       </ul></dd></dl>
     *                       <p/>
     *                       <dl><dt><b>Postconditions:</b></dt><dd>
     *                       <ul><li>the state of the receiver is updated to reflect the XML element
     *                       parsed from the reader
     *                       </ul></dd></dl><dl>
     * @throws XMLParseException If an error occured while parsing the string.
     */
    public void parseString(String string,
                            int offset,
                            int end,
                            int startingLineNr)
            throws XMLParseException {
        string = string.substring(offset, end);
        try {
            this.parseFromChars(string.toCharArray(), startingLineNr);
        } catch (IOException e) {
            // Java exception handling suxx
        }
    }


    /**
     * Reads one XML element from a char array and parses it.
     *
     * @param reader
     *     The reader from which to retrieve the XML data.
     * @param offset
     *     The first character in <code>string</code> to scan.
     * @param end
     *     The character where to stop scanning.
     *     This character is not scanned.
     *
     * </dl><dl><dt><b>Preconditions:</b></dt><dd>
     * <ul><li><code>input != null</code>
     *     <li><code>end &lt;= input.length</code>
     *     <li><code>offset &lt; end</code>
     *     <li><code>offset &gt;= 0</code>
     * </ul></dd></dl>
     *
     * <dl><dt><b>Postconditions:</b></dt><dd>
     * <ul><li>the state of the receiver is updated to reflect the XML element
     *         parsed from the reader
     * </ul></dd></dl><dl>
     *
     * @throws XMLParseException
     *     If an error occured while parsing the string.
     */
//    public void parseFromChars(char[] input,
//                               int    offset,
//                               int    end)
//        throws net.epzilla.accumulator.util.XMLParseException
//    {
//        this.parseFromChars(input, offset, end, /*startingLineNr*/ 1);
//    }


    /**
     * Reads one XML element from a char array and parses it.
     *
     * @param reader
     *     The reader from which to retrieve the XML data.
     * @param offset
     *     The first character in <code>string</code> to scan.
     * @param end
     *     The character where to stop scanning.
     *     This character is not scanned.
     * @param startingLineNr
     *     The line number of the first line in the data.
     *
     * </dl><dl><dt><b>Preconditions:</b></dt><dd>
     * <ul><li><code>input != null</code>
     *     <li><code>end &lt;= input.length</code>
     *     <li><code>offset &lt; end</code>
     *     <li><code>offset &gt;= 0</code>
     * </ul></dd></dl>
     *
     * <dl><dt><b>Postconditions:</b></dt><dd>
     * <ul><li>the state of the receiver is updated to reflect the XML element
     *         parsed from the reader
     * </ul></dd></dl><dl>
     *
     * @throws XMLParseException
     *     If an error occured while parsing the string.
     */
//    public void parseCharArray(char[] input,
//                               int    offset,
//                               int    end,
//                               int    startingLineNr)
//        throws net.epzilla.accumulator.util.XMLParseException
//    {
//        try {
//            com.isi.terminal.Reader reader = new CharArrayReader(input, offset, end);
//            this.parseFromReader(reader, startingLineNr);
//        } catch (IOException e) {
//            // This exception will never happen.
//        }
//    }


    /**
     * Removes a child element.
     *
     * @param child
     *     The child element to remove.
     *
     * </dl><dl><dt><b>Preconditions:</b></dt><dd>
     * <ul><li><code>child != null</code>
     *     <li><code>child</code> is a child element of the receiver
     * </ul></dd></dl>
     *
     * <dl><dt><b>Postconditions:</b></dt><dd>
     * <ul><li>countChildren() => old.countChildren() - 1
     *     <li>enumerateChildren() => old.enumerateChildren() - child
     *     <li>getChildren() => old.enumerateChildren() - child
     * </ul></dd></dl><dl>
     *
     *  net.epzilla.accumulator.util.XMLElement#addChild(nanoxml.net.epzilla.accumulator.util.XMLElement)
     *         addChild(net.epzilla.accumulator.util.XMLElement)
     *  net.epzilla.accumulator.util.XMLElement#countChildren()
     *  net.epzilla.accumulator.util.XMLElement#enumerateChildren()
     *  net.epzilla.accumulator.util.XMLElement#getChildren()
     */
//    public void removeChild(net.epzilla.accumulator.util.XMLElement child)
//    {
//        this.children.removeElement(child);
//    }


    /**
     * Removes an attribute.
     *
     * @param name
     *     The name of the attribute.
     *
     * </dl><dl><dt><b>Preconditions:</b></dt><dd>
     * <ul><li><code>name != null</code>
     *     <li><code>name</code> is a valid XML identifier
     * </ul></dd></dl>
     *
     * <dl><dt><b>Postconditions:</b></dt><dd>
     * <ul><li>enumerateAttributeNames()
     *         => old.enumerateAttributeNames() - name
     *     <li>getAttribute(name) => <code>null</code>
     * </ul></dd></dl><dl>
     *
     *  net.epzilla.accumulator.util.XMLElement#enumerateAttributeNames()
     *  net.epzilla.accumulator.util.XMLElement#setDoubleAttribute(java.lang.String, double)
     *         setDoubleAttribute(String, double)
     *  net.epzilla.accumulator.util.XMLElement#setIntAttribute(java.lang.String, int)
     *         setIntAttribute(String, int)
     *  net.epzilla.accumulator.util.XMLElement#setAttribute(java.lang.String, java.lang.Object)
     *         setAttribute(String, Object)
     *  net.epzilla.accumulator.util.XMLElement#getAttribute(java.lang.String)
     *         getAttribute(String)
     *  net.epzilla.accumulator.util.XMLElement#getAttribute(java.lang.String, java.lang.Object)
     *         getAttribute(String, Object)
     *  net.epzilla.accumulator.util.XMLElement#getAttribute(java.lang.String,
     *                                      java.util.Hashtable,
     *                                      java.lang.String, boolean)
     *         getAttribute(String, Hashtable, String, boolean)
     *  net.epzilla.accumulator.util.XMLElement#getStringAttribute(java.lang.String)
     *         getStringAttribute(String)
     *  net.epzilla.accumulator.util.XMLElement#getStringAttribute(java.lang.String,
     *                                            java.lang.String)
     *         getStringAttribute(String, String)
     *  net.epzilla.accumulator.util.XMLElement#getStringAttribute(java.lang.String,
     *                                            java.util.Hashtable,
     *                                            java.lang.String, boolean)
     *         getStringAttribute(String, Hashtable, String, boolean)
     *  net.epzilla.accumulator.util.XMLElement#getIntAttribute(java.lang.String)
     *         getIntAttribute(String)
     *  net.epzilla.accumulator.util.XMLElement#getIntAttribute(java.lang.String, int)
     *         getIntAttribute(String, int)
     *  net.epzilla.accumulator.util.XMLElement#getIntAttribute(java.lang.String,
     *                                         java.util.Hashtable,
     *                                         java.lang.String, boolean)
     *         getIntAttribute(String, Hashtable, String, boolean)
     *  net.epzilla.accumulator.util.XMLElement#getDoubleAttribute(java.lang.String)
     *         getDoubleAttribute(String)
     *  net.epzilla.accumulator.util.XMLElement#getDoubleAttribute(java.lang.String, double)
     *         getDoubleAttribute(String, double)
     *  net.epzilla.accumulator.util.XMLElement#getDoubleAttribute(java.lang.String,
     *                                            java.util.Hashtable,
     *                                            java.lang.String, boolean)
     *         getDoubleAttribute(String, Hashtable, String, boolean)
     *  net.epzilla.accumulator.util.XMLElement#getBooleanAttribute(java.lang.String,
     *                                             java.lang.String,
     *                                             java.lang.String, boolean)
     *         getBooleanAttribute(String, String, String, boolean)
     */
//    public void removeAttribute(String name)
//    {
//        if (this.ignoreCase) {
//            name = name.toUpperCase();
//        }
//        this.attributes.remove(name);
//    }


    /**
     * Removes an attribute.
     *
     * @param name
     *     The name of the attribute.
     *
     * @deprecated Use {@link #removeAttribute(java.lang.String)
     *             removeAttribute} instead.
     */
//    public void removeProperty(String name)
//    {
//        this.removeAttribute(name);
//    }


    /**
     * Removes an attribute.
     *
     * @param name
     *     The name of the attribute.
     *
     * @deprecated Use {@link #removeAttribute(java.lang.String)
     *             removeAttribute} instead.
     */
//    public void removeChild(String name)
//    {
//        this.removeAttribute(name);
//    }


    /**
     * Creates a new similar XML element.
     * <p/>
     * You should override this method when subclassing net.epzilla.accumulator.util.XMLElement.
     */
    protected XMLElement createAnotherElement() {
        return new XMLElement(this.entities,
                this.ignoreWhitespace,
                false,
                this.ignoreCase);
    }


    /**
     * Changes the content string.
     *
     * @param content The new content string.
     */
    public void setContent(String content) {
        this.contents = content;
    }


    /**
     * Changes the name of the element.
     *
     * @param name The new name.
     * @deprecated Use {@link #setName(java.lang.String) setName} instead.
     */
    public void setTagName(String name) {
        this.setName(name);
    }


    /**
     * Changes the name of the element.
     *
     * @param name The new name.
     *             <p/>
     *             </dl><dl><dt><b>Preconditions:</b></dt><dd>
     *             <ul><li><code>name != null</code>
     *             <li><code>name</code> is a valid XML identifier
     *             </ul></dd></dl>
     *             <p/>
     *             net.epzilla.accumulator.util.XMLElement#getName()
     */
    public void setName(String name) {
        this.name = name;
    }


    /**
     * Writes the XML element to a string.
     * <p/>
     * net.epzilla.accumulator.util.XMLElement#write(java.io.Writer) write(Writer)
     */
    public String toString() {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            OutputStreamWriter writer = new OutputStreamWriter(out);
            this.write(writer);
            writer.flush();
            return new String(out.toByteArray());
        } catch (IOException e) {
            // Java exception handling suxx
            return super.toString();
        }
    }


    /**
     * Writes the XML element to a writer.
     *
     * @param writer The writer to write the XML data to.
     *               <p/>
     *               </dl><dl><dt><b>Preconditions:</b></dt><dd>
     *               <ul><li><code>writer != null</code>
     *               <li><code>writer</code> is not closed
     *               </ul></dd></dl>
     * @throws java.io.IOException If the data could not be written to the writer.
     *                             <p/>
     *                             net.epzilla.accumulator.util.XMLElement#toString()
     */

    public void write(Writer writer)
            throws IOException {
        write(writer, 0);
    }

    private void write(Writer writer, int tab)
            throws IOException {

        if (this.name == null) {
            this.writeEncoded(writer, this.contents);
            return;
        }
        for (int i = 0; i < tab; i++) {
            writer.write('\t');
        }
        writer.write('<');
        writer.write(this.name);
        if (!this.attributes.isEmpty()) {
            Enumeration enume = this.attributes.keys();
            while (enume.hasMoreElements()) {
                writer.write(' ');
                String key = (String) enume.nextElement();
                String value = this.attributes.get(key);
                writer.write(key);
                writer.write('=');
                writer.write('"');
                this.writeEncoded(writer, value);
                writer.write('"');
            }
        }
        if ((this.contents != null) && (this.contents.length() > 0)) {
            writer.write('>');
            this.writeEncoded(writer, this.contents);
            writer.write('<');
            writer.write('/');
            writer.write(this.name);
            writer.write(">\n");
        } else if (this.children.isEmpty()) {
            writer.write('/');
            writer.write(">\n");
        } else {
            writer.write(">\n");
            Iterator<XMLElement> enume = this.enumerateChildren();
            tab++;
            while (enume.hasNext()) {
                XMLElement child = enume.next();
                child.write(writer, tab);
            }
            writer.write('<');
            writer.write('/');
            writer.write(this.name);
            writer.write(">\n");
        }
    }


    /**
     * Writes a string encoded to a writer.
     *
     * @param writer The writer to write the XML data to.
     * @param str    The string to write encoded.
     *               <p/>
     *               </dl><dl><dt><b>Preconditions:</b></dt><dd>
     *               <ul><li><code>writer != null</code>
     *               <li><code>writer</code> is not closed
     *               <li><code>str != null</code>
     *               </ul></dd></dl>
     */
    protected void writeEncoded(Writer writer,
                                String str)
            throws IOException {
        for (int i = 0; i < str.length(); i += 1) {
            char ch = str.charAt(i);
            switch (ch) {
                case '<':
                    writer.write('&');
                    writer.write('l');
                    writer.write('t');
                    writer.write(';');
                    break;
                case '>':
                    writer.write('&');
                    writer.write('g');
                    writer.write('t');
                    writer.write(';');
                    break;
                case '&':
                    writer.write('&');
                    writer.write('a');
                    writer.write('m');
                    writer.write('p');
                    writer.write(';');
                    break;
                case '"':
                    writer.write('&');
                    writer.write('q');
                    writer.write('u');
                    writer.write('o');
                    writer.write('t');
                    writer.write(';');
                    break;
                case '\'':
                    writer.write('&');
                    writer.write('a');
                    writer.write('p');
                    writer.write('o');
                    writer.write('s');
                    writer.write(';');
                    break;
                default:
                    int unicode = (int) ch;
                    if ((unicode < 32) || (unicode > 126)) {
                        writer.write('&');
                        writer.write('#');
                        writer.write('x');
                        writer.write(Integer.toString(unicode, 16));
                        writer.write(';');
                    } else {
                        writer.write(ch);
                    }
            }
        }
    }


    /**
     * Scans an identifier from the current reader.
     * The scanned identifier is appended to <code>result</code>.
     *
     * @param result The buffer in which the scanned identifier will be put.
     *               <p/>
     *               </dl><dl><dt><b>Preconditions:</b></dt><dd>
     *               <ul><li><code>result != null</code>
     *               <li>The next character read from the reader is a valid first
     *               character of an XML identifier.
     *               </ul></dd></dl>
     *               <p/>
     *               <dl><dt><b>Postconditions:</b></dt><dd>
     *               <ul><li>The next character read from the reader won't be an identifier
     *               character.
     *               </ul></dd></dl><dl>
     */
    protected void scanIdentifier(StringBuffer result)
            throws IOException {
        for (; ;) {
            char ch = this.readChar();
            if (((ch < 'A') || (ch > 'Z')) && ((ch < 'a') || (ch > 'z'))
                    && ((ch < '0') || (ch > '9')) && (ch != '_') && (ch != '.')
                    && (ch != ':') && (ch != '-') && (ch <= '\u007E')) {
                this.unreadChar(ch);
                return;
            }
            result.append(ch);
        }
    }


    /**
     * This method scans an identifier from the current reader.
     *
     * @return the next character following the whitespace.
     */
    protected char scanWhitespace()
            throws IOException {
        for (; ;) {
            char ch = this.readChar();
            switch (ch) {
                case ' ':
                case '\t':
                case '\n':
                case '\r':
                    break;
                default:
                    return ch;
            }
        }
    }


    /**
     * This method scans an identifier from the current reader.
     * The scanned whitespace is appended to <code>result</code>.
     *
     * @return the next character following the whitespace.
     *         <p/>
     *         </dl><dl><dt><b>Preconditions:</b></dt><dd>
     *         <ul><li><code>result != null</code>
     *         </ul></dd></dl>
     */
    protected char scanWhitespace(StringBuffer result)
            throws IOException {
        for (; ;) {
            char ch = this.readChar();
            switch (ch) {
                case ' ':
                case '\t':
                case '\n':
                    result.append(ch);
                case '\r':
                    break;
                default:
                    return ch;
            }
        }
    }


    /**
     * This method scans a delimited string from the current reader.
     * The scanned string without delimiters is appended to
     * <code>string</code>.
     * <p/>
     * </dl><dl><dt><b>Preconditions:</b></dt><dd>
     * <ul><li><code>string != null</code>
     * <li>the next char read is the string delimiter
     * </ul></dd></dl>
     */
    protected void scanString(StringBuffer string)
            throws IOException {
        char delimiter = this.readChar();
        if ((delimiter != '\'') && (delimiter != '"')) {
            throw this.expectedInput("' or \"");
        }
        for (; ;) {
            char ch = this.readChar();
            if (ch == delimiter) {
                return;
            } else if (ch == '&') {
                this.resolveEntity(string);
            } else {
                string.append(ch);
            }
        }
    }


    /**
     * Scans a #PCDATA element. CDATA sections and entities are resolved.
     * The next &lt; char is skipped.
     * The scanned data is appended to <code>data</code>.
     * <p/>
     * </dl><dl><dt><b>Preconditions:</b></dt><dd>
     * <ul><li><code>data != null</code>
     * </ul></dd></dl>
     */
    protected void scanPCData(StringBuffer data)
            throws IOException {
        for (; ;) {
            char ch = this.readChar();
            if (ch == '<') {
                ch = this.readChar();
                if (ch == '!') {
                    this.checkCDATA(data);
                } else {
                    this.unreadChar(ch);
                    return;
                }
            } else if (ch == '&') {
                this.resolveEntity(data);
            } else {
                data.append(ch);
            }
        }
    }


    /**
     * Scans a special tag and if the tag is a CDATA section, append its
     * content to <code>buf</code>.
     * <p/>
     * </dl><dl><dt><b>Preconditions:</b></dt><dd>
     * <ul><li><code>buf != null</code>
     * <li>The first &lt; has already been read.
     * </ul></dd></dl>
     */
    protected boolean checkCDATA(StringBuffer buf)
            throws IOException {
        char ch = this.readChar();
        if (ch != '[') {
            this.unreadChar(ch);
            this.skipSpecialTag(0);
            return false;
        } else if (!this.checkLiteral("CDATA[")) {
            this.skipSpecialTag(1); // one [ has already been read
            return false;
        } else {
            int delimiterCharsSkipped = 0;
            while (delimiterCharsSkipped < 3) {
                ch = this.readChar();
                switch (ch) {
                    case ']':
                        if (delimiterCharsSkipped < 2) {
                            delimiterCharsSkipped += 1;
                        } else {
                            buf.append(']');
                            buf.append(']');
                            delimiterCharsSkipped = 0;
                        }
                        break;
                    case '>':
                        if (delimiterCharsSkipped < 2) {
                            for (int i = 0; i < delimiterCharsSkipped; i++) {
                                buf.append(']');
                            }
                            delimiterCharsSkipped = 0;
                            buf.append('>');
                        } else {
                            delimiterCharsSkipped = 3;
                        }
                        break;
                    default:
                        for (int i = 0; i < delimiterCharsSkipped; i += 1) {
                            buf.append(']');
                        }
                        buf.append(ch);
                        delimiterCharsSkipped = 0;
                }
            }
            return true;
        }
    }


    /**
     * Skips a comment.
     * <p/>
     * </dl><dl><dt><b>Preconditions:</b></dt><dd>
     * <ul><li>The first &lt;!-- has already been read.
     * </ul></dd></dl>
     */
    protected void skipComment()
            throws IOException {
        int dashesToRead = 2;
        while (dashesToRead > 0) {
            char ch = this.readChar();
            if (ch == '-') {
                dashesToRead -= 1;
            } else {
                dashesToRead = 2;
            }
        }
        if (this.readChar() != '>') {
            throw this.expectedInput(">");
        }
    }


    /**
     * Skips a special tag or comment.
     *
     * @param bracketLevel The number of open square brackets ([) that have
     *                     already been read.
     *                     <p/>
     *                     </dl><dl><dt><b>Preconditions:</b></dt><dd>
     *                     <ul><li>The first &lt;! has already been read.
     *                     <li><code>bracketLevel >= 0</code>
     *                     </ul></dd></dl>
     */
    protected void skipSpecialTag(int bracketLevel)
            throws IOException {
        int tagLevel = 1; // <
        char stringDelimiter = '\0';
        if (bracketLevel == 0) {
            char ch = this.readChar();
            if (ch == '[') {
                bracketLevel += 1;
            } else if (ch == '-') {
                ch = this.readChar();
                if (ch == '[') {
                    bracketLevel += 1;
                } else if (ch == ']') {
                    bracketLevel -= 1;
                } else if (ch == '-') {
                    this.skipComment();
                    return;
                }
            }
        }
        while (tagLevel > 0) {
            char ch = this.readChar();
            if (stringDelimiter == '\0') {
                if ((ch == '"') || (ch == '\'')) {
                    stringDelimiter = ch;
                } else if (bracketLevel <= 0) {
                    if (ch == '<') {
                        tagLevel += 1;
                    } else if (ch == '>') {
                        tagLevel -= 1;
                    }
                }
                if (ch == '[') {
                    bracketLevel += 1;
                } else if (ch == ']') {
                    bracketLevel -= 1;
                }
            } else {
                if (ch == stringDelimiter) {
                    stringDelimiter = '\0';
                }
            }
        }
    }


    /**
     * Scans the data for literal text.
     * Scanning stops when a character does not match or after the complete
     * text has been checked, whichever comes first.
     *
     * @param literal the literal to check.
     *                <p/>
     *                </dl><dl><dt><b>Preconditions:</b></dt><dd>
     *                <ul><li><code>literal != null</code>
     *                </ul></dd></dl>
     */
    protected boolean checkLiteral(String literal)
            throws IOException {
        int length = literal.length();
        for (int i = 0; i < length; i += 1) {
            if (this.readChar() != literal.charAt(i)) {
                return false;
            }
        }
        return true;
    }


    /**
     * Reads a character from a reader.
     */
    protected char readChar()
            throws IOException {
        if (this.charReadTooMuch != '\0') {
            char ch = this.charReadTooMuch;
            this.charReadTooMuch = '\0';
            return ch;
        } else {
            try {
                int i = this.reader[charIndex++];
                //				if (i < 0) {
                //					throw this.unexpectedEndOfData();
                //				}
                if (i == 10) {
                    this.parserLineNr += 1;
                    return '\n';
                } else {
                    return (char) i;
                }
            }
            catch (Exception ex) {
                throw new IOException("Array Index out of boiund");
            }
        }
    }


    /**
     * Scans an XML element.
     *
     * @param elt The element that will contain the result.
     *            <p/>
     *            </dl><dl><dt><b>Preconditions:</b></dt><dd>
     *            <ul><li>The first &lt; has already been read.
     *            <li><code>elt != null</code>
     *            </ul></dd></dl>
     */
    protected void scanElement(XMLElement elt)
            throws IOException {
        StringBuffer buf = new StringBuffer();
        this.scanIdentifier(buf);
        String name = buf.toString();
        elt.setName(name);
        char ch = this.scanWhitespace();
        while ((ch != '>') && (ch != '/')) {
            buf.setLength(0);
            this.unreadChar(ch);
            this.scanIdentifier(buf);
            String key = buf.toString();
            ch = this.scanWhitespace();
            if (ch != '=') {
                throw this.expectedInput("=");
            }
            this.unreadChar(this.scanWhitespace());
            buf.setLength(0);
            this.scanString(buf);
            elt.setAttribute(key, buf);
            ch = this.scanWhitespace();
        }
        if (ch == '/') {
            ch = this.readChar();
            if (ch != '>') {
                throw this.expectedInput(">");
            }
            return;
        }
        buf.setLength(0);
        ch = this.scanWhitespace(buf);
        if (ch != '<') {
            this.unreadChar(ch);
            this.scanPCData(buf);
        } else {
            for (; ;) {
                ch = this.readChar();
                if (ch == '!') {
                    if (this.checkCDATA(buf)) {
                        this.scanPCData(buf);
                        break;
                    } else {
                        ch = this.scanWhitespace(buf);
                        if (ch != '<') {
                            this.unreadChar(ch);
                            this.scanPCData(buf);
                            break;
                        }
                    }
                } else {
                    if ((ch != '/') || this.ignoreWhitespace) {
                        buf.setLength(0);
                    }
                    if (ch == '/') {
                        this.unreadChar(ch);
                    }
                    break;
                }
            }
        }
        if (buf.length() == 0) {
            while (ch != '/') {
                if (ch == '!') {
                    ch = this.readChar();
                    if (ch != '-') {
                        throw this.expectedInput("Comment or Element");
                    }
                    ch = this.readChar();
                    if (ch != '-') {
                        throw this.expectedInput("Comment or Element");
                    }
                    this.skipComment();
                } else {
                    this.unreadChar(ch);
                    XMLElement child = this.createAnotherElement();
                    this.scanElement(child);
                    elt.addChild(child);
                }
                ch = this.scanWhitespace();
                if (ch != '<') {
                    throw this.expectedInput("<");
                }
                ch = this.readChar();
            }
            this.unreadChar(ch);
        } else {
            if (this.ignoreWhitespace) {
                elt.setContent(buf.toString().trim());
            } else {
                elt.setContent(buf.toString());
            }
        }
        ch = this.readChar();
        if (ch != '/') {
            throw this.expectedInput("/");
        }
        this.unreadChar(this.scanWhitespace());
        if (!this.checkLiteral(name)) {
            throw this.expectedInput(name);
        }
        if (this.scanWhitespace() != '>') {
            throw this.expectedInput(">");
        }
    }


    /**
     * Resolves an entity. The name of the entity is read from the reader.
     * The value of the entity is appended to <code>buf</code>.
     *
     * @param buf Where to put the entity value.
     *            <p/>
     *            </dl><dl><dt><b>Preconditions:</b></dt><dd>
     *            <ul><li>The first &amp; has already been read.
     *            <li><code>buf != null</code>
     *            </ul></dd></dl>
     */
    protected void resolveEntity(StringBuffer buf)
            throws IOException {
        char ch = '\0';
        StringBuffer keyBuf = new StringBuffer();
        for (; ;) {
            ch = this.readChar();
            if (ch == ';') {
                break;
            }
            keyBuf.append(ch);
        }
        String key = keyBuf.toString();
        if (key.charAt(0) == '#') {
            try {
                if (key.charAt(1) == 'x') {
                    ch = (char) Integer.parseInt(key.substring(2), 16);
                } else {
                    ch = (char) Integer.parseInt(key.substring(1), 10);
                }
            } catch (NumberFormatException e) {
                throw this.unknownEntity(key);
            }
            buf.append(ch);
        } else {
            char[] value = (char[]) this.entities.get(key);
            if (value == null) {
                throw this.unknownEntity(key);
            }
            buf.append(value);
        }
    }


    /**
     * Pushes a character back to the read-back buffer.
     *
     * @param ch The character to push back.
     *           <p/>
     *           </dl><dl><dt><b>Preconditions:</b></dt><dd>
     *           <ul><li>The read-back buffer is empty.
     *           <li><code>ch != '\0'</code>
     *           </ul></dd></dl>
     */
    protected void unreadChar(char ch) {
        this.charReadTooMuch = ch;
    }


    /**
     * Creates a parse exception for when an invalid valueset is given to
     * a method.
     *
     * @param name The name of the entity.
     *             <p/>
     *             </dl><dl><dt><b>Preconditions:</b></dt><dd>
     *             <ul><li><code>name != null</code>
     *             </ul></dd></dl>
     */
    protected XMLParseException invalidValueSet(String name) {
        String msg = "Invalid value set (entity name = \"" + name + "\")";
        return new XMLParseException(this.getName(), this.parserLineNr, msg);
    }


    /**
     * Creates a parse exception for when an invalid value is given to a
     * method.
     *
     * @param name  The name of the entity.
     * @param value The value of the entity.
     *              <p/>
     *              </dl><dl><dt><b>Preconditions:</b></dt><dd>
     *              <ul><li><code>name != null</code>
     *              <li><code>value != null</code>
     *              </ul></dd></dl>
     */
    protected XMLParseException invalidValue(String name,
                                             String value) {
        String msg = "Attribute \"" + name + "\" does not contain a valid "
                + "value (\"" + value + "\")";
        return new XMLParseException(this.getName(), this.parserLineNr, msg);
    }


    /**
     * Creates a parse exception for when the end of the data input has been
     * reached.
     */
    protected XMLParseException unexpectedEndOfData() {
        String msg = "Unexpected end of data reached";
        return new XMLParseException(this.getName(), this.parserLineNr, msg);
    }


    /**
     * Creates a parse exception for when a syntax error occured.
     *
     * @param context The context in which the error occured.
     *                <p/>
     *                </dl><dl><dt><b>Preconditions:</b></dt><dd>
     *                <ul><li><code>context != null</code>
     *                <li><code>context.length() &gt; 0</code>
     *                </ul></dd></dl>
     */
    protected XMLParseException syntaxError(String context) {
        String msg = "Syntax error while parsing " + context;
        return new XMLParseException(this.getName(), this.parserLineNr, msg);
    }


    /**
     * Creates a parse exception for when the next character read is not
     * the character that was expected.
     *
     * @param charSet The set of characters (in human readable form) that was
     *                expected.
     *                <p/>
     *                </dl><dl><dt><b>Preconditions:</b></dt><dd>
     *                <ul><li><code>charSet != null</code>
     *                <li><code>charSet.length() &gt; 0</code>
     *                </ul></dd></dl>
     */
    protected XMLParseException expectedInput(String charSet) {
        String msg = "Expected: " + charSet;
        return new XMLParseException(this.getName(), this.parserLineNr, msg);
    }


    /**
     * Creates a parse exception for when an entity could not be resolved.
     *
     * @param name The name of the entity.
     *             <p/>
     *             </dl><dl><dt><b>Preconditions:</b></dt><dd>
     *             <ul><li><code>name != null</code>
     *             <li><code>name.length() &gt; 0</code>
     *             </ul></dd></dl>
     */
    protected XMLParseException unknownEntity(String name) {
        String msg = "Unknown or invalid entity: &" + name + ";";
        return new XMLParseException(this.getName(), this.parserLineNr, msg);
    }

}
