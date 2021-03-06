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
package org.apache.commons.jxpath.ri;


/**
 * A qualified name: a combination of an optional namespace prefix
 * and an local name.
 *
 * @author Dmitri Plotnikov
 * @version $Revision$ $Date$
 */
public class QName {
    private String prefix;
    private String name;

    public QName(String qualifiedName) {
        int index = qualifiedName.indexOf(':');
        if (index == -1) {
            prefix = null;
            name = qualifiedName;
        }
        else {
            prefix = qualifiedName.substring(0, index);
            name = qualifiedName.substring(index + 1);
        }
    }

    public QName(String prefix, String localName) {
        this.prefix = prefix;
        this.name = localName;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        if (prefix != null) {
            return prefix + ':' + name;
        }
        return name;
    }

    public int hashCode() {
        return name.hashCode();
    }

    public boolean equals(Object object) {
        if (!(object instanceof QName)) {
            return false;
        }
        if (this == object) {
            return true;
        }
        QName that = (QName) object;
        if (!this.name.equals(that.name)) {
            return false;
        }

        if ((this.prefix == null && that.prefix != null)
            || (this.prefix != null && !this.prefix.equals(that.prefix))) {
            return false;
        }

        return true;
    }
}