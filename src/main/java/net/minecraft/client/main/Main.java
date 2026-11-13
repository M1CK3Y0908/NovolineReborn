package net.minecraft.client.main;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.authlib.properties.PropertyMap.Serializer;
import java.io.File;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.util.List;
import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.NonOptionArgumentSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;

public class Main {
   private static boolean isNullOrEmpty(String str) {
      return str != null && !str.isEmpty();
   }

   public static void main(String[] args) {
      System.setProperty("java.net.preferIPv4Stack", "true");
      noop();
      OptionParser optionParser = new OptionParser();
      optionParser.allowsUnrecognizedOptions();
      optionParser.accepts("demo");
      optionParser.accepts("fullscreen");
      optionParser.accepts("checkGlErrors");
      ArgumentAcceptingOptionSpec argumentAcceptingOptionSpec = optionParser.accepts("server").withRequiredArg();
      ArgumentAcceptingOptionSpec argumentAcceptingOptionSpec2 = optionParser.accepts("port").withRequiredArg().ofType(Integer.class).defaultsTo(25565);
      ArgumentAcceptingOptionSpec argumentAcceptingOptionSpec3 = optionParser.accepts("gameDir").withRequiredArg().ofType(File.class).defaultsTo(new File("."));
      ArgumentAcceptingOptionSpec argumentAcceptingOptionSpec4 = optionParser.accepts("assetsDir").withRequiredArg().ofType(File.class);
      ArgumentAcceptingOptionSpec argumentAcceptingOptionSpec5 = optionParser.accepts("resourcePackDir").withRequiredArg().ofType(File.class);
      ArgumentAcceptingOptionSpec argumentAcceptingOptionSpec6 = optionParser.accepts("proxyHost").withRequiredArg();
      ArgumentAcceptingOptionSpec argumentAcceptingOptionSpec7 = optionParser.accepts("proxyPort").withRequiredArg().defaultsTo("8080").ofType(Integer.class);
      ArgumentAcceptingOptionSpec argumentAcceptingOptionSpec8 = optionParser.accepts("proxyUser").withRequiredArg();
      ArgumentAcceptingOptionSpec argumentAcceptingOptionSpec9 = optionParser.accepts("proxyPass").withRequiredArg();
      ArgumentAcceptingOptionSpec argumentAcceptingOptionSpec10 = optionParser.accepts("username").withRequiredArg().defaultsTo("Player" + Minecraft.getSystemTime() % 1000L);
      ArgumentAcceptingOptionSpec argumentAcceptingOptionSpec11 = optionParser.accepts("uuid").withRequiredArg();
      ArgumentAcceptingOptionSpec argumentAcceptingOptionSpec12 = optionParser.accepts("accessToken").withRequiredArg().required();
      ArgumentAcceptingOptionSpec argumentAcceptingOptionSpec13 = optionParser.accepts("version").withRequiredArg().required();
      ArgumentAcceptingOptionSpec argumentAcceptingOptionSpec14 = optionParser.accepts("width").withRequiredArg().ofType(Integer.class).defaultsTo(854);
      ArgumentAcceptingOptionSpec argumentAcceptingOptionSpec15 = optionParser.accepts("height").withRequiredArg().ofType(Integer.class).defaultsTo(480);
      ArgumentAcceptingOptionSpec argumentAcceptingOptionSpec16 = optionParser.accepts("userProperties").withRequiredArg().defaultsTo("{}");
      ArgumentAcceptingOptionSpec argumentAcceptingOptionSpec17 = optionParser.accepts("profileProperties").withRequiredArg().defaultsTo("{}");
      ArgumentAcceptingOptionSpec argumentAcceptingOptionSpec18 = optionParser.accepts("assetIndex").withRequiredArg();
      ArgumentAcceptingOptionSpec argumentAcceptingOptionSpec19 = optionParser.accepts("userType").withRequiredArg().defaultsTo("legacy");
      NonOptionArgumentSpec nonOptionArgumentSpec = optionParser.nonOptions();
      OptionSet optionSet = optionParser.parse(args);
      List list = optionSet.valuesOf(nonOptionArgumentSpec);
      if (!list.isEmpty()) {
         System.out.println("Completely ignored arguments: " + list);
      }

      String text = (String) optionSet.valueOf(argumentAcceptingOptionSpec6);
      Proxy proxy = Proxy.NO_PROXY;
      if (text != null) {
         try {
            proxy = new Proxy(Type.SOCKS, new InetSocketAddress(text, (int) optionSet.valueOf(argumentAcceptingOptionSpec7)));
         } catch (Exception exception) {
         }
      }

      final String text2 = (String) optionSet.valueOf(argumentAcceptingOptionSpec8);
      final String text5 = (String) optionSet.valueOf(argumentAcceptingOptionSpec9);
      if (!proxy.equals(Proxy.NO_PROXY) && isNullOrEmpty(text2) && isNullOrEmpty(text5)) {
         Authenticator.setDefault(new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
               return new PasswordAuthentication(text2, text5.toCharArray());
            }
         });
      }

      int i = (int) optionSet.valueOf(argumentAcceptingOptionSpec14);
      int i2 = (int) optionSet.valueOf(argumentAcceptingOptionSpec15);
      boolean flag = optionSet.has("fullscreen");
      boolean flag2 = optionSet.has("checkGlErrors");
      boolean flag3 = optionSet.has("demo");
      String text3 = (String) optionSet.valueOf(argumentAcceptingOptionSpec13);
      Gson gson2 = new GsonBuilder().registerTypeAdapter(PropertyMap.class, new Serializer()).create();
      PropertyMap propertyMap = gson2.fromJson((String)optionSet.valueOf(argumentAcceptingOptionSpec16), PropertyMap.class);
      PropertyMap propertyMap2 = gson2.fromJson((String)optionSet.valueOf(argumentAcceptingOptionSpec17), PropertyMap.class);
      File file = (File) optionSet.valueOf(argumentAcceptingOptionSpec3);
      File file2 = optionSet.has(argumentAcceptingOptionSpec4) ? (File) optionSet.valueOf(argumentAcceptingOptionSpec4) : new File(file, "assets/");
      File file3 = optionSet.has(argumentAcceptingOptionSpec5) ? (File) optionSet.valueOf(argumentAcceptingOptionSpec5) : new File(file, "resourcepacks/");
      String text4 = optionSet.has(argumentAcceptingOptionSpec11) ? (String)argumentAcceptingOptionSpec11.value(optionSet) : (String)argumentAcceptingOptionSpec10.value(optionSet);
      String text6 = optionSet.has(argumentAcceptingOptionSpec18) ? (String)argumentAcceptingOptionSpec18.value(optionSet) : null;
      String text7 = (String) optionSet.valueOf(argumentAcceptingOptionSpec);
      Integer integer = (Integer) optionSet.valueOf(argumentAcceptingOptionSpec2);
      Session session = new Session((String)argumentAcceptingOptionSpec10.value(optionSet), text4, (String)argumentAcceptingOptionSpec12.value(optionSet), (String)argumentAcceptingOptionSpec19.value(optionSet));
      GameConfiguration gameConfiguration = new GameConfiguration(
         new GameConfiguration.UserInformation(session, propertyMap, propertyMap2, proxy),
         new GameConfiguration.DisplayInformation(i, i2, flag, flag2),
         new GameConfiguration.FolderInformation(file, file3, file2, text6),
         new GameConfiguration.GameInformation(flag3, text3),
         new GameConfiguration.ServerInformation(text7, integer)
      );
      Runtime.getRuntime().addShutdownHook(new Thread("Client Shutdown Thread") {
         @Override
         public void run() {
            Minecraft.stopIntegratedServer();
         }
      });
      Thread.currentThread().setName("Client thread");
      new Minecraft(gameConfiguration).run();
   }

   private static void noop() {
   }
}
