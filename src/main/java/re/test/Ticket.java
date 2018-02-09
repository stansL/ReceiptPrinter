package re.test;

/*
 * Ticket.java
 *
 * Copyright 2013 Josue Camara <picharras@picharras-HP-Folio>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301, USA.
 *
 */

import java.awt.*;
import java.awt.print.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.Doc;
import javax.print.ServiceUI;
import javax.print.attribute.*;

public class Ticket {

    //Ticket attribute content
    private String contentTicket =
                    "        ***** START OF LEGAL RECEIPT ***** \n" +
                    "               MERU SAFI DISHES \n" +
                    "           REGISTER NO: KE0001311460375\n" +
                    "           P.O.BOX 195 MERU, KENYA.\n" +
                    "          EMAIL: SAFIDISHES@GMAIL.COM\n" +
                    "                  VAT: 1538446C\n" +
                    "               PIN NO: A002256972P\n\n" +

//                    "USR NO: 0002            KRA/EFP/010522013/20257B\n" +
                    "USR NO: 0002            KRA/ETR/26952009/20283k\n" +
                    String.format("TILL #: %-15s %s", "01", "CASH SALE #: 190\n") +
                    String.format("DATE  : %-15s %s", "25-JAN-18", "TIME:  08:02 PM\n") +

                    "================================================\n" +
                    String.format("%-25s %5s %7s %7s\n", "Item", "Qty", "Price", "Amount") +
                    "================================================\n\n" +
                    "toreplace" +
                    "================================================\n" +
                    String.format("%-23s %20s\n", "TOTAL:", "TT") +
                    String.format("%-23s %20s\n", "CASH:", "CC") +
                    String.format("%-23s %20s\n", "CHANGE:", "XX") +
                    "================================================\n" +
                    "PRICES INCLUSIVE OF VAT WHERE APPLICABLE";

    //El constructor que setea los valores a la instancia
    Ticket(String regnumber, String nameLocal, String expedition, String box, String ticket, String caissier, String dateTime, String subTotal, String tax, String total, String recibo, String change) {
        List<Item> items = new ArrayList<Item>();
        double cashInHand = 1000;
        items.add(new Item("JH201", "Cocktail Juice", 1, 220));
        items.add(new Item("JH201", "Chapati", 1, 50));
        items.add(new Item("FF093", "1 Ltre Fresh Water", 1, 150));
        items.add(new Item("GC234", "Fish Stew", 1, 550));

        String toreplace = getToReplace(items);
        this.contentTicket = this.contentTicket.replace("toreplace", toreplace);
        this.contentTicket = this.contentTicket.replace("{{regnumber}}", regnumber);
        this.contentTicket = this.contentTicket.replace("{{nameLocal}}", nameLocal);
        this.contentTicket = this.contentTicket.replace("{{expedition}}", expedition);
        this.contentTicket = this.contentTicket.replace("{{box}}", box);
        this.contentTicket = this.contentTicket.replace("{{ticket}}", ticket);
        this.contentTicket = this.contentTicket.replace("{{cajero}}", caissier);
        this.contentTicket = this.contentTicket.replace("{{dateTime}}", dateTime);
        this.contentTicket = this.contentTicket.replace("{{subTotal}}", subTotal);
        this.contentTicket = this.contentTicket.replace("{{tax}}", tax);
        this.contentTicket = this.contentTicket.replace("CC", String.valueOf(cashInHand));
        double itemSum = items.stream().mapToDouble(value -> value.getQuantity() * value.getUnitPrice()).sum();
        this.contentTicket = this.contentTicket.replace("TT", String.valueOf(itemSum));
        this.contentTicket = this.contentTicket.replace("{{recibo}}", recibo);
        this.contentTicket = this.contentTicket.replace("XX", String.valueOf(cashInHand - itemSum));


    }

    private String getToReplace(List<Item> items) {
        String toReplace = new String();
        for (Item item : items) {
            toReplace += String.format("%-25s %5s %7s %7s\n", item.getName(), item.getQuantity(), item.getUnitPrice(), item.getUnitPrice() * item.getQuantity());
        }
        return toReplace;
    }

    public void print() {
        //Especificamos el tipo de dato a imprimir
        //Tipo: bytes; Subtipo: autodetectado
        DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;

        //Aca obtenemos el servicio de impresion por defatul
        //Si no quieres ver el dialogo de seleccionar impresora usa esto
        //PrintService defaultService = PrintServiceLookup.lookupDefaultPrintService();


        //Con esto mostramos el dialogo para seleccionar impresora
        //Si quieres ver el dialogo de seleccionar impresora usalo
        //Solo mostrara las impresoras que soporte arreglo de bits
        PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
        PrintService printService[] = PrintServiceLookup.lookupPrintServices(flavor, pras);
        PrintService defaultService = PrintServiceLookup.lookupDefaultPrintService();
        PrintService service = ServiceUI.printDialog(null, 700, 200, printService, defaultService, flavor, pras);

        //Creamos un arreglo de tipo byte
        byte[] bytes;

        //Aca convertimos el string(cuerpo del ticket) a bytes tal como
        //lo maneja la impresora(mas bien ticketera :p)
        bytes = this.contentTicket.getBytes();
        byte[] cutP = new byte[]{0x1d, 'V', 1};
        byte[] destination = new byte[bytes.length + cutP.length];

        System.arraycopy(bytes, 0, destination, 0, bytes.length);

// copy mac into end of destination (from pos ciphertext.length, copy mac.length bytes)
        System.arraycopy(cutP, 0, destination, bytes.length, cutP.length);

        //Creamos un documento a imprimir, a el se le appendeara
        //el arreglo de bytes
        Doc doc = new SimpleDoc(destination, flavor, null);

        //Creamos un trabajo de impresión
        DocPrintJob job = service.createPrintJob();

        //Imprimimos dentro de un try de a huevo
        try {
            //El metodo print imprime
            job.print(doc, null);
//
//            Doc cutDoc = new SimpleDoc(cutP, flavor, null);
//            job.print(doc, null);
        } catch (Exception er) {
            JOptionPane.showMessageDialog(null, "Error al imprimir: " + er.getMessage());
        }
    }

}