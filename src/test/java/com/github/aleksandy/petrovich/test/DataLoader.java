package com.github.aleksandy.petrovich.test;

import static java.nio.charset.StandardCharsets.*;

import java.io.*;
import java.net.*;
import java.net.Proxy.Type;
import java.text.MessageFormat;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

public class DataLoader {

    public static void main(String[] args) throws Exception {
//        System.setProperty("http.proxyHost", "172.31.33.113");
//        System.setProperty("http.proxyPort", "8081");

        String[] namesDicts = {"/female.names"/*, "/male.names"*/};

        JAXBContext jaxbContext = JAXBContext.newInstance(MorpherResult.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        for (String dict : namesDicts) {
            try (
                BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                        DataLoader.class.getResourceAsStream(dict),
                        UTF_8
                    )
                );
                BufferedWriter out = new BufferedWriter(
                    new OutputStreamWriter(
                        new FileOutputStream(dict.substring(1) + ".res", true),
                        UTF_8
                    )
                );
            ) {
                String name = null;
                while ((name = in.readLine()) != null) {
                    try (
                        Reader nameReader = new InputStreamReader(
                            getDeclesions(name),
                            UTF_8
                        );
                    ) {
                        MorpherResult declesions = (MorpherResult) unmarshaller.unmarshal(nameReader);

                        out.append(name).append('|')
                            .append(declesions.getGenitive()).append('|')
                            .append(declesions.getDative()).append('|')
                            .append(declesions.getAccusative()).append('|')
                            .append(declesions.getInstrumental()).append('|')
                            .append(declesions.getPrepositional()).append('\n')
                            .flush();
                    } catch (JAXBException e) {
                        System.err.println(e.getMessage());
                    }

//                    Thread.sleep(2_000);
                }
            }
        }

    }

    private static MessageFormat format = (
        new MessageFormat("http://api.morpher.ru/WebService.asmx/GetDeclension?s={0}")
    );

    private static InputStream getDeclesions(String name) throws IOException, MalformedURLException {
        String url = format.format(new String[] {URLEncoder.encode(name, "UTF-8")});
        Proxy proxy = createProxy();
        System.out.printf("get declensions url: %s%nusing proxy: %s%n", url, proxy);
        URLConnection conn = new URL(url).openConnection(proxy);
        return conn.getInputStream();
    }

    private static Proxy createProxy() {
        String host = System.getProperty("http.proxyHost", "");
        if (!host.isEmpty()) {
            SocketAddress address = new InetSocketAddress(
                host, Integer.parseInt(
                    System.getProperty("http.proxyPort")
                )
            );
            return new Proxy(Type.HTTP, address);
        }

        return Proxy.NO_PROXY;
    }


}
