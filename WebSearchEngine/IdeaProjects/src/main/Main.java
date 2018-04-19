/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import crawler.Crawler;

import java.sql.SQLException;


public class Main {
    static int docMax = 100;
    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        Crawler.crawl(docMax);
    }
}
