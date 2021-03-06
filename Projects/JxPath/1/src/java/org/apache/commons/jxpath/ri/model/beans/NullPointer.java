/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.jxpath.ri.model.beans;

import java.util.Locale;

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.ri.QName;
import org.apache.commons.jxpath.ri.model.NodePointer;

/**
 * @author Dmitri Plotnikov
 * @version $Revision$ $Date$
 */
public class NullPointer extends PropertyOwnerPointer {
    private QName name;
    private String id;

    public NullPointer(QName name, Locale locale) {
        super(null, locale);
        this.name = name;
    }

    /**
     * Used for the root node
     */
    public NullPointer(NodePointer parent, QName name) {
        super(parent);
        this.name = name;
    }

    public NullPointer(Locale locale, String id) {
        super(null, locale);
        this.id = id;
    }

    public QName getName() {
        return name;
    }

    public Object getBaseValue() {
        return null;
    }
    
    public boolean isCollection() {
        return false;
    }

    public boolean isLeaf() {
        return true;
    }        

    public boolean isActual() {
        return false;
    }

    public PropertyPointer getPropertyPointer() {
        return new NullPropertyPointer(this);
    }

    public NodePointer createPath(JXPathContext context, Object value) {
        if (parent != null) {
            return parent.createPath(context, value).getValuePointer();
        }
        else {
            throw new UnsupportedOperationException(
                "Cannot create the root object: " + asPath());
        }
    }

    public NodePointer createPath(JXPathContext context) {
        if (parent != null) {
            return parent.createPath(context).getValuePointer();
        }
        else {
            throw new UnsupportedOperationException(
                "Cannot create the root object: " + asPath());
        }
    }

    public NodePointer createChild(
        JXPathContext context,
        QName name,
        int index) 
    {
        return createPath(context).createChild(context, name, index);
    }

    public NodePointer createChild(
        JXPathContext context,
        QName name, 
        int index,
        Object value) 
    {
        return createPath(context).createChild(context, name, index, value);
    }

    public int hashCode() {
        return name == null ? 0 : name.hashCode();
    }

    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }

        if (!(object instanceof NullPointer)) {
            return false;
        }

        NullPointer other = (NullPointer) object;
        return (name == null && other.name == null)
            || (name != null && name.equals(other.name));
    }

    public String asPath() {
        if (id != null) {
            return "id(" + id + ")";
        }

        if (parent != null) {
            return super.asPath();
        }
        return "null()";
    }

    public int getLength() {
        return 0;
    }
}