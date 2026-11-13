package wtf.novoline.utils.account;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;
import org.tinylog.Logger;
import wtf.novoline.Novoline;
import wtf.novoline.util.ExchangeCodeResponse;
import wtf.novoline.util.MCProfileResponse;
import wtf.novoline.util.XBLAuthenticationResponse;
import wtf.novoline.utils.http.HttpExecutor;
import wtf.novoline.utils.http.MicrosoftTokenRequest;
import wtf.novoline.utils.http.MinecraftLoginRequest;
import wtf.novoline.utils.http.MinecraftProfileRequest;
import wtf.novoline.utils.http.UriUtils;
import wtf.novoline.utils.http.XboxLiveAuthRequest;
import wtf.novoline.utils.http.XboxLiveAuthRequest.XBLProperties;
import wtf.novoline.utils.http.XboxLiveAuthRequest.XSTSProperties;

public class MicrosoftAuthenticator {
   private Process process;
   private CompletableFuture<String> completableFuture;
   private final HttpServer httpServer;

   {
      try {
         this.httpServer = HttpServer.create();
      } catch (java.io.IOException exception) {
         throw wtf.novoline.support.SneakyThrow.sneak(exception);
      }
   }
   private final HttpExecutor httpExecutor = new HttpExecutor();
   private final MicrosoftClientId microsoftClientId;

   private HttpHandler authHandler() {
      return httpExchange -> {
         Map map = UriUtils.parseQueryString(httpExchange.getRequestURI());
         if (map.containsKey("code")) {
            this.completableFuture.complete((String)map.get("code"));
            this.respondNoContent(httpExchange);
         } else {
            this.respond(httpExchange, "Code was not supplied", 400);
         }

         this.shutdown();
      };
   }

   public ExchangeCodeResponse exchangeCode(String text) throws IOException, InterruptedException {
      return (ExchangeCodeResponse)this.httpExecutor
         .executeJson(
            MicrosoftTokenRequest.getMicrosoftTokenRequest()
               .clientId(this.microsoftClientId.getClientId())
               .code(text)
               .grantType("authorization_code")
               .redirectUri("https://login.live.com/oauth20_desktop.srf")
               .scope("service::user.auth.xboxlive.com::MBI_SSL"),
            ExchangeCodeResponse.class
         );
   }

   public MicrosoftOAuthToken getMicrosoftOAuthToken(String text, String text2) throws IOException, InterruptedException {
      return (MicrosoftOAuthToken)this.httpExecutor
         .executeJson(MinecraftLoginRequest.create().getMinecraftLoginRequest("XBL3.0 x=" + text + ";" + text2), MicrosoftOAuthToken.class);
   }

   public Process getProcess() {
      return this.process;
   }

   public MicrosoftAuthenticator(MicrosoftClientId microsoftClientId2) {
      this.microsoftClientId = microsoftClientId2;
   }

   private int getRandomPort() {
      return ThreadLocalRandom.current().nextInt(40100, 40200);
   }

   public ExchangeCodeResponse getExchangeCodeResponse(String text) throws IOException, InterruptedException {
      return (ExchangeCodeResponse)this.httpExecutor
         .executeJson(
            MicrosoftTokenRequest.getMicrosoftTokenRequest()
               .clientId(this.microsoftClientId.getClientId())
               .grantType("refresh_token")
               .scope("service::user.auth.xboxlive.com::MBI_SSL")
               .redirectUri("https://login.live.com/oauth20_desktop.srf")
               .refreshToken(text),
            ExchangeCodeResponse.class
         );
   }

   private void respond(HttpExchange httpExchange2, String text, int i) throws IOException {
      if (text == null) {
         httpExchange2.sendResponseHeaders(i, -1L);
      } else {
         byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
         httpExchange2.getResponseBody().write(bytes);
         httpExchange2.sendResponseHeaders(i, bytes.length);
      }

      httpExchange2.close();
   }

   public XBLAuthenticationResponse authenticateXboxLive(String text) throws IOException, InterruptedException {
      return (XBLAuthenticationResponse)this.httpExecutor
         .executeJson(
            XboxLiveAuthRequest.createXboxLiveAuthRequest(URI.create("https://user.auth.xboxlive.com/user/authenticate"))
               .getXboxLiveAuthRequest(XBLProperties.getXBLProperties().authMethod("RPS").siteName("user.auth.xboxlive.com").rpsTicket(text))
               .relyingParty("http://auth.xboxlive.com")
               .tokenType("JWT"),
            XBLAuthenticationResponse.class
         );
   }

   public CompletableFuture getCompletableFuture() throws IOException {
      if (this.completableFuture != null && !this.completableFuture.isCancelled()) {
         throw new IllegalStateException("Authentication is already started!");
      }

      this.startServer();
      String text = "http://127.0.0.1:" + this.httpServer.getAddress().getPort() + "/authentication_callback";
      URI uri = UriUtils.createURI(
         "https://login.live.com/oauth20_authorize.srf",
         Map.of(
            "client_id",
            this.microsoftClientId.getClientId(),
            "response_type",
            "code",
            "redirect_uri",
            "https://login.live.com/oauth20_desktop.srf",
            "scope",
            "service::user.auth.xboxlive.com::MBI_SSL"
         )
      );
      WebViewLauncher.deleteDefaultProfile();
      Logger.info("Starting WebView with URI {}, LS: {}", uri.toString(), text);
      this.process = WebViewLauncher.createProcess(uri.toString(), "^https:\\/\\/login\\.live\\.com\\/oauth20_desktop\\.srf\\?code=(.*)&lc=(.*)$", text);
      this.completableFuture = new CompletableFuture<>();
      return this.completableFuture;
   }

   public MicrosoftAuthenticator() {
      this(new MicrosoftClientId());
   }

   public void startServer() throws IOException {
      int i = 0;

      while (true) {
         try {
            InetSocketAddress inetSocketAddress = new InetSocketAddress("127.0.0.1", this.getRandomPort());
            this.httpServer.bind(inetSocketAddress, 0);
            break;
         } catch (Exception exception2) {
            if (++i == 6) {
               throw new IOException("Unable to start HTTP server");
            }

            Novoline.taggedLogger.error(exception2, "Unable to bind server ({}/{}), retrying!", i, 5);
         }
      }

      Novoline.taggedLogger.debug("Started server on port {}!", new Object[]{this.httpServer.getAddress().getPort()});
      this.httpServer.start();
      this.httpServer.createContext("/", this.authHandler());
   }

   public void stopServer() {
      Novoline.taggedLogger.debug("Server is stopping NOW");
      this.httpServer.stop(0);
   }

   public void shutdown() {
      this.stopServer();
      if (this.process != null) {
         this.process.destroy();
      }

      if (!this.completableFuture.isDone()) {
         this.completableFuture.cancel(true);
      }
   }

   private void respondNoContent(HttpExchange httpExchange2) throws IOException {
      this.respond(httpExchange2, null, 204);
   }

   public XBLAuthenticationResponse getXBLAuthenticationResponse(String text) throws IOException, InterruptedException {
      return (XBLAuthenticationResponse)this.httpExecutor
         .executeJson(
            XboxLiveAuthRequest.createXboxLiveAuthRequest(URI.create("https://xsts.auth.xboxlive.com/xsts/authorize"))
               .getXboxLiveAuthRequest(XSTSProperties.getXSTSProperties().sandboxTicket("RETAIL").userTokens(Collections.singletonList(text)))
               .relyingParty("rp://api.minecraftservices.com/")
               .tokenType("JWT"),
            XBLAuthenticationResponse.class
         );
   }

   public MCProfileResponse getMCProfileResponse(String text, String text2) throws IOException, InterruptedException {
      return (MCProfileResponse)this.httpExecutor.executeJson(MinecraftProfileRequest.createMinecraftProfileRequest(text, text2), MCProfileResponse.class);
   }
}
