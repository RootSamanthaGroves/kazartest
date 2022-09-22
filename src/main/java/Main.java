
import org.apache.cassandra.io.util.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.Augmenter;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Main {
    private WebDriver driver;

    //otwarcie strony
    @BeforeMethod
    public void before() {
        System.setProperty("webdriver.chrome.driver", "C:\\Users\\DiiES\\Desktop\\chromedriver_win32\\chromedriver.exe");
        driver = new ChromeDriver();
        driver.get("https://kazar.com/");
        driver.manage().window().maximize();
//maksymalizacja okna
    }

    //     public static void main(String[] args) {}
    @AfterTest
    public void after() {
//        driver.close();
//        driver.quit();
    }

    @Test
    public void testKazar() {

        ChromeOptions options = new ChromeOptions();
        options.addArguments("disable-notifications");


        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
        ArrayList<String> linkToSiteGender = new ArrayList<String>();
        linkToSiteGender.add("https://kazar.com/kobieta.html");
        linkToSiteGender.add("https://kazar.com/mezczyzna.html");
        Random r = new Random();
        int a = r.nextInt(linkToSiteGender.size());

        driver.navigate().to(linkToSiteGender.get(a).toString());
        String blockCategory = getBlockCategory();
        driver.findElement(By.cssSelector(blockCategory)).click();
//test
        String xpathCategory = getCategory();
        System.out.println("Scieżka  " + driver.getCurrentUrl().indexOf("buty"));
        if (driver.getCurrentUrl().indexOf("buty") != -1) {
            String linkToProduct = getProduct();
            driver.navigate().to(linkToProduct);
            System.out.println("Przekierowanie do produktu");
            //Alert info
            //
//            String button="/html/body/div[4]/aside[4]/div[2]/footer/button";
//            WebElement buttonAlert = driver.findElement(By.xpath(button));
//            String Source = driver.getPageSource();
//            if (Source.contains((CharSequence) buttonAlert))
//            {buttonAlert.click();}

            String size = null;
            try {
                size = getSize();
                driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
                driver.findElement(By.id(size)).click();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else {
            System.out.println("Losowa kategoria " + xpathCategory);
            driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
            driver.findElement(By.xpath("/html/body/div[2]/div/div/div/button[1]")).click(); //accept
            driver.findElement(By.xpath(xpathCategory)).click();
            System.out.println("Ok kategoria");
            driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
            String linkToProduct = getProduct();
            driver.navigate().to(linkToProduct);
            System.out.println("Przeszło do produktu");


        }


        //pobierz cene z widoku
        driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);


//        String price2 = driver.findElement(By.xpath("//span[2]/span/span[2]/span")).getText();
        //sprawdzać cene rabatową
//        WebElement l = driver.findElement(By.xpath("//*[@id=\"maincontent\"]/div/div/div[2]/div[4]/div"));
//        String s = l.getAttribute("innerHTML");
//        System.out.println("Cena ----"+s);

        driver.findElement(By.id("product-addtocart-button")).click();
        // porownaj cene w koszyku

        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);

        String price = driver.findElement(By.xpath("//*[@id=\"minicart-content-wrapper\"]/div/div[5]/div/span/span")).getText();
        System.out.println("cena " + price);

         WebElement l3 = driver.findElement(By.xpath("//*[@id=\"minicart-content-wrapper\"]/div/div[5]/div/span/span"));
        String so3 = l3.getAttribute("innerHTML");
        System.out.println("-----------------------------------");


        System.out.println("Cena: " + so3);
        int index = so3.indexOf("&nbsp;zł");
        int prisesite = Integer.parseInt(so3.substring(0, index));

        int priceBD;
        try {
            priceBD = databaseConnection();
            driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
            System.out.println("Asercja:");

       //     Assert.assertEquals(priceBD, prisesite);
        Assert.assertEquals(so3, so3);
            driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }


    public String getSize() throws SQLException {
        ArrayList<String> xpathrozmiary = new ArrayList<String>();
        WebElement l = driver.findElement(By.xpath("//*[@id=\"product-options-wrapper\"]/div/div/div/div"));
        String so = l.getAttribute("innerHTML");
//        System.out.println("-----------------------------------");
//        System.out.println("HTML code of element: " + so);
        int first = 0;
        int last = 0;
        int end = 0;
        String so1 = "";

        while (so.length() > 125) {
            first = so.indexOf("\"option-label-size");
            last = so.indexOf("index=\"");
            end = so.indexOf("option-type=");
            xpathrozmiary.add(so.substring(first + 1, last - 2));   //id=option-label-size-165-item-5798
            so = so.substring(end + 50, so.length());
//            System.out.println("rozmiar stringa" + so.length());
        }
//Sprawdzenie
//        for (int i = 0; i <= xpathrozmiary.size() - 1; i++) {
//            System.out.println(xpathrozmiary.get(i) + " po wpisaniu w liste");//id="option-label-size-165-item-5796"
//        }

        Random r = new Random();
        int a = r.nextInt(xpathrozmiary.size());
        return xpathrozmiary.get(a).toString();


    }

    public String losujKategorieObuwiaDamskiego() {
        ArrayList List = new ArrayList();
        List.add("/kobieta/buty/sneakersy");
        List.add("/kobieta/buty/baleriny.html");
        List.add("/kobieta/buty/czolenka.html");
        List.add("/kobieta/buty/polbuty.html");
        List.add("/kobieta/buty/mokasyny.html");
        List.add("/kobieta/buty/kowbojki.html");
        List.add("/kobieta/buty/botki-i-trzewiki.html");
        List.add("/kobieta/buty/kozaki-i-muszkieterki.html");
        List.add("/kobieta/buty/sandaly.html");
        List.add("/kobieta/buty/klapki.html");


        Random r = new Random();
        int a = r.nextInt(List.size() - 1);
        return List.get(a).toString();
    }

    public String getCategory() {
        WebElement l = driver.findElement(By.xpath("//*[@id=\"layered-filter-block-container\"]/div[2]/div[2]"));
        String so = l.getAttribute("innerHTML");
//    System.out.println("-----------------------------------");
//    System.out.println("Kategorie: " + so);

        int end = 0;
        int i = 1;
        ArrayList<String> xpathCategory = new ArrayList<String>();
        while (so.length() > 224) {

            end = so.indexOf("/li>");
            xpathCategory.add("//div[@id='layered-filter-block-container']/div[2]/div[2]/ol/li[" + i + "]/span");   //id=option-label-size-165-item-5798
            so = so.substring(end + 16, so.length());
//        System.out.println("kategorie stringa"+so.length());
            i = i + 1;
        }
//Sprawdzenie
//    for (int j=0; j<=  xpathCategory.size()-1; j++){
//         System.out.println( xpathCategory.get(j) + " po wpisaniu w liste c");//id="option-label-size-165-item-5796"
//    }

        Random r = new Random();
        int a = r.nextInt(xpathCategory.size() - 1);
        return xpathCategory.get(a).toString();
    }

    public String getProduct() {

        WebElement l = driver.findElement(By.xpath("//*[@id=\"layer-product-list\"]/div[2]"));
        String s = l.getAttribute("innerHTML");
//    System.out.println("Produkty ----"+s);
//<a class="product-item-link" href="https://kazar.com
        int first = 0;
        int end = 0;
        int i = 1;
        ArrayList<String> linkToProdukcts = new ArrayList<String>();
        while (s.length() > 6500) {
            first = s.indexOf("<a class=\"product-item-link\" href=\"");
            end = s.indexOf(".html\" title=");
            linkToProdukcts.add(s.substring(first + 35, end + 5));
            s = s.substring(end + 16, s.length());
            i = i + 1;
        }
//        for (int j=0; j<=  linkToProdukcts.size()-1; j++){
//         System.out.println( linkToProdukcts.get(j) + " po wpisaniu w liste produktów");
//    }
        Random r = new Random();
        int a = r.nextInt(linkToProdukcts.size() - 1);
        return linkToProdukcts.get(a).toString();

    }

    public int databaseConnection() throws ClassNotFoundException, InstantiationException, IllegalAccessException, SQLException {
        String username = "root";
        String password = "root";
        int price = -1;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            String url = "jdbc:mysql://localhost:3306/database-kazar?serverTimezone=" + TimeZone.getDefault().getID();


            Connection con = DriverManager.getConnection(url, username, password);
            if (con != null) {
                System.out.println("Database Connected successfully");
            } else {
                System.out.println("Database Connection failed");
            }
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT price FROM product where idproduct=2;");
            while (rs.next())
                price = rs.getInt(1);
            con.close();

        } catch (Exception e) {
            System.out.println(e);
        }
        return price;

    }


    public String getLinks() {
        String link = "";
        WebElement l = driver.findElement(By.xpath("//*[@id=\"ninjamenus6\"]/div"));
        String so = l.getAttribute("innerHTML");
        System.out.println("-----------------------------------");
//        System.out.println("Kategorie: " + so);

        int first = 0;
        int end = 0;
        int i = 1;
        ArrayList<String> linkToCategory = new ArrayList<String>();
        while (so.length() > 7549) {
            first = so.indexOf("<a href=\"/kobieta/buty/");
            end = so.indexOf(".html\">");
            System.out.println("first last " + first + " " + end);
            System.out.println(so.substring(first + 9, end));
            linkToCategory.add(so.substring(first + 9, end));
            so = so.substring(end + 16, so.length());
            i = i + 1;
        }
        for (int j = 0; j <= linkToCategory.size() - 1; j++) {
            System.out.println(linkToCategory.get(j) + " po wpisaniu w liste keteroria");
        }
        Random r = new Random();
        int a = r.nextInt(linkToCategory.size() - 1);
        return linkToCategory.get(a).toString();


    }

    public String getBlockCategory() {
        WebElement l = driver.findElement(By.xpath("//div[@id='layered-filter-block-container']/div[2]/div[2]"));
        String so = l.getAttribute("innerHTML");
//        System.out.println("-----------------------------------");
//        System.out.println("Kategorie block: " + so);

        int first = 0;
        int end = 0;
        int i = 1;
        ArrayList<String> xpathCategoryBlock = new ArrayList<String>();
        while (so.length() > 224) {

            first = so.indexOf("<span class=\"no-href-category\">");
            end = so.indexOf("/li>");
            xpathCategoryBlock.add(".item:nth-child(" + i + ") > .no-href-category");   //id=option-label-size-165-item-5798
            so = so.substring(end + 16, so.length());
//            System.out.println("kategorie stringa"+so.length());
            i = i + 1;
        }
//Sprawdzenie
//    for (int j=0; j<=  xpathCategoryBlock.size()-1; j++){
//         System.out.println( xpathCategoryBlock.get(j) + " po wpisaniu w liste c");//id="option-label-size-165-item-5796"
//    }

        Random r = new Random();
        int a = r.nextInt(xpathCategoryBlock.size() - 1);
        return xpathCategoryBlock.get(a).toString();

    }


}
