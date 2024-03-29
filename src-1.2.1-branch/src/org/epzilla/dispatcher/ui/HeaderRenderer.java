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
package org.epzilla.dispatcher.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;

class HeaderRenderer
    extends DefaultTableCellRenderer
{
    public HeaderRenderer()
    {
        setHorizontalAlignment(SwingConstants.CENTER);
        setOpaque(true);

        // This call is needed because DefaultTableCellRenderer calls setBorder()
        // in its constructor, which is executed after updateUI()
        setBorder(UIManager.getBorder("TableHeader.cellBorder"));
    }

    public void updateUI()
    {
        super.updateUI();
        setBorder(UIManager.getBorder("TableHeader.cellBorder"));
    }

    public Component getTableCellRendererComponent(JTable table, Object value,
                       boolean selected, boolean focused, int row, int column)
    {
        JTableHeader h = table != null ? table.getTableHeader() : null;

        if (h != null)
        {
            setEnabled(h.isEnabled());
            setComponentOrientation(h.getComponentOrientation());

            setForeground(Color.white);
            setBackground(Color.black);
            setFont(h.getFont());
        }
        else
        {
            /* Use sensible values instead of random leftover values from the last call */
            setEnabled(true);
            setComponentOrientation(ComponentOrientation.UNKNOWN);

            setForeground(Color.white);
            setBackground(Color.black);
            setFont(UIManager.getFont("TableHeader.font"));
        }

        setValue(value);

        return this;
    }
}
