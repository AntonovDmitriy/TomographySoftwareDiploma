/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.antonov.tomographysoftwarediploma.impl;

import java.io.IOException;
import java.io.PrintWriter;

/**
 *
 * @author Antonov
 */
public class NewClass {
    
    public static void main(String[] args) throws IOException {
            Process p = Runtime.getRuntime().exec("cmd.exe");
        PrintWriter stdin = new PrintWriter(p.getOutputStream());
        stdin.println("cd " + "//");
        stdin.println("Signer.js");
        stdin.close();    
        
        ProcessBuilder builder = new ProcessBuilder(
            "cmd.exe", "/c", "cd \"C:\\Program Files\\Microsoft SQL Server\" && dir");
        builder.redirectErrorStream(true);
        Process ps = builder.start();
    }
    
    
}
