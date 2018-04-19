package re.test;

import javax.swing.*;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

public class Test {

    public static void main(String[] args) {
//        System.out.println("Test test coming here!!");
////        PrinterService printerService = new PrinterService();
////        System.out.println(printerService.getPrinters());
////        //print some stuff
////        printerService.printString("80mm Series Printer", stuffToPrint());
////        printerService.printString("Portable Printer DPP-250", stuffToPrint());
////        // cut that paper!
////        byte[] cutP = new byte[]{0x1d, 'V', 1};
////        printerService.printBytes("80mm Series Printer", cutP);
        Ticket ticket = new Ticket("KE0000316460045","namelocal","expedition","box","ticket","cashier",
                "datetime","sfdsfd","safdaf","sdafafd","sdfafd","change");
        ticket.print();
    }


    public static String stuffToPrint() {

        return anotherTest();
    }

    public static String anotherTest() {
        String s = String.format("%-15s %5s %10s %10s\n", "Item", "Qty", "Price", "Total");
        String s1 = String.format("%-15s %5s %10s %10s\n", "----", "---", "-----", "-----");
        String output = s + s1;

        System.out.println(output);
        return output;
    }
}
