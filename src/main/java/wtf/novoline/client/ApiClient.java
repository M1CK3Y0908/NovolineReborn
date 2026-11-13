package wtf.novoline.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import wtf.novoline.client.PostResourcesRatingsRequest.PositiveType;
import wtf.novoline.client.discord.GatewayChatMessage;
import wtf.novoline.client.discord.GatewayClient;
import wtf.novoline.client.discord.Snowflake;
import wtf.novoline.gson.CosmeticDeserializer;
import wtf.novoline.gson.InstantStringDeserializer;
import wtf.novoline.gson.LongValueTypeAdapter;
import wtf.novoline.util.AuthenticationResponse;
import wtf.novoline.util.Profile;
import wtf.novoline.util.RestClientOptions;
import wtf.novoline.utils.config.CloudListResult;
import wtf.novoline.utils.config.CloudScript;
import wtf.novoline.utils.config.ConfigComment;
import wtf.novoline.utils.cosmetic.Cosmetic;

public class ApiClient {
   private final RestClientOptions restClientOptions;
   private static final Gson gson = new GsonBuilder()
      .registerTypeAdapter(Instant.class, new InstantStringDeserializer())
      .registerTypeAdapter(Cosmetic.class, new CosmeticDeserializer())
      .registerTypeAdapter(Snowflake.class, new LongValueTypeAdapter())
      .create();
   private GatewayClient gatewayClient;
   private ApiRequestHandler apiRequestHandler;
   private final ApiHttpDispatcher apiHttpDispatcher;

   public CompletableFuture<Void> deleteResourceComment(DeleteResourcesCommentsRequest deleteResourcesCommentsRequest) {
      return this.executeVoid(deleteResourcesCommentsRequest);
   }

   public RestClientOptions getRestClientOptions() {
      return this.restClientOptions;
   }

   public void setGatewayClient(GatewayClient gatewayClient2) {
      this.gatewayClient = gatewayClient2;
   }

   public CompletableFuture<Collection<GatewayChatMessage>> getRoomRecentMessages(GetRoomsRecentMessagesRequest getRoomsRecentMessagesRequest) {
      Type type = (new TypeToken<LinkedList<GatewayChatMessage>>() {}).getType();
      return this.requestJson(getRoomsRecentMessagesRequest, type);
   }

   public CompletableFuture<Void> postResourceRating(PostResourcesRatingsRequest postResourcesRatingsRequest) {
      return this.executeVoid(postResourcesRatingsRequest);
   }

   public CompletableFuture uploadFile(String text, byte[] bArray) {
      return this.uploadFile(FileUploadRequest.createFileUploadRequest(text, bArray));
   }

   public CompletableFuture deleteResourceRating(String text) {
      return this.deleteResourceRating(DeleteResourcesRatingsRequest.createDeleteResourcesRatingsRequest(text));
   }

   public CompletableFuture<GatewayChatMessage> sendRoomMessage(PutRoomsMessagesRequest putRoomsMessagesRequest) {
      return this.requestJson(putRoomsMessagesRequest, GatewayChatMessage.class);
   }

   public GatewayClient getGatewayClient() {
      return this.gatewayClient;
   }

   public CompletableFuture deleteResource(String text) {
      return this.deleteResource(DeleteResourcesRequest.createDeleteResourcesRequest(text));
   }

   public CompletableFuture<ConfigComment> editResourceComment(PatchResourcesCommentsRequest patchResourcesCommentsRequest) {
      return this.requestJson(patchResourcesCommentsRequest, ConfigComment.class);
   }

   public CompletableFuture<Void> editResource(PatchResourcesRequest patchResourcesRequest) {
      return this.executeVoid(patchResourcesRequest);
   }

   private <T> CompletableFuture<T> requestJson(ApiRequest apiRequest, Type type) {
      return this.dispatch(apiRequest, java.net.http.HttpResponse.BodyHandlers.ofString(), this.jsonDeserializer(type));
   }

   public ApiHttpDispatcher getApiHttpDispatcher() {
      return this.apiHttpDispatcher;
   }

   private CompletableFuture<Void> executeVoid(ApiRequest apiRequest) {
      return this.dispatch(apiRequest, java.net.http.HttpResponse.BodyHandlers.ofString(), httpResponse -> null);
   }

   public CompletableFuture<Void> deleteResourceRating(DeleteResourcesRatingsRequest deleteResourcesRatingsRequest) {
      return this.executeVoid(deleteResourcesRatingsRequest);
   }

   public CompletableFuture updatePresence(PatchUsersSelfPresenceRequest patchUsersSelfPresenceRequest) {
      this.requireGatewayConnection();
      return this.executeVoid(patchUsersSelfPresenceRequest.withSid(this.gatewayClient.getHeartbeatState().getSid()));
   }

   public CompletableFuture<CloudListResult> listResources(GetResourcesRequest2 getResourcesRequest2) {
      return this.requestJson(getResourcesRequest2, CloudListResult.class);
   }

   public ApiClient(RestClientOptions restClientOptions2) {
      this.restClientOptions = restClientOptions2;
      this.apiHttpDispatcher = ApiHttpDispatcher.createApiHttpDispatcher(this);
   }

   public CompletableFuture rateResource(String text, PositiveType positiveType) {
      return this.postResourceRating(PostResourcesRatingsRequest.createPostResourcesRatingsRequest(text, positiveType));
   }

   public CompletableFuture authenticate(String text) {
      return CompletableFuture.completedFuture(new AuthenticationResponse("1"));
   }

   public CompletableFuture<InputStream> getCosmeticTexture(GetProfilesCosmeticsTexturesRequest getProfilesCosmeticsTexturesRequest) {
      return this.dispatch(getProfilesCosmeticsTexturesRequest, java.net.http.HttpResponse.BodyHandlers.ofInputStream(), HttpResponse::body);
   }

   private void requireGatewayConnection() {
      if (this.gatewayClient.getHeartbeatState() == null) {
         throw new IllegalStateException("Not connected to Gateway");
      }
   }

   public CompletableFuture<CloudScript> createResource(PutResourcesRequest putResourcesRequest) {
      return this.requestJson(putResourcesRequest, CloudScript.class);
   }

   public CompletableFuture<ConfigComment> createResourceComment(PutResourcesCommentsRequest putResourcesCommentsRequest) {
      return this.requestJson(putResourcesCommentsRequest, ConfigComment.class);
   }

   public CompletableFuture<AuthenticationResponse> login(PostAuthenticationLoginRequest postAuthenticationLoginRequest) {
      return this.requestJson(postAuthenticationLoginRequest, AuthenticationResponse.class);
   }

   public CompletableFuture getResource(String text) {
      return this.getResource(GetResourcesRequest.createGetResourcesRequest(text));
   }

   public void setApiRequestHandler(ApiRequestHandler apiRequestHandler2) {
      this.apiRequestHandler = apiRequestHandler2;
   }

   public CompletableFuture downloadResourceFile(String text) {
      return this.downloadResourceFile(GetResourcesFileRequest.createGetResourcesFileRequest(text));
   }

   public CompletableFuture<CloudScript> getResource(GetResourcesRequest getResourcesRequest) {
      return this.requestJson(getResourcesRequest, CloudScript.class);
   }

   private <T, U> CompletableFuture<T> dispatch(ApiRequest apiRequest, java.net.http.HttpResponse.BodyHandler<U> bodyHandler, Function<HttpResponse<U>, T> function2) {
      return this.apiHttpDispatcher.sendAsync(apiRequest.authentication(this.apiRequestHandler), bodyHandler).thenApply(function2);
   }

   public CompletableFuture<Profile> getProfile(GetProfilesRequest getProfilesRequest) {
      return this.requestJson(getProfilesRequest, Profile.class);
   }

   public ApiRequestHandler getApiRequestHandler() {
      return this.apiRequestHandler;
   }

   private <R> Function<HttpResponse<String>, R> jsonDeserializer(Type type) {
      return httpResponse -> gson.fromJson(httpResponse.body(), type);
   }

   public CompletableFuture<Void> deleteResource(DeleteResourcesRequest deleteResourcesRequest) {
      return this.executeVoid(deleteResourcesRequest);
   }

   public CompletableFuture getCosmeticTexture(String text) {
      return this.getCosmeticTexture(GetProfilesCosmeticsTexturesRequest.createGetProfilesCosmeticsTexturesRequest(text));
   }

   public CompletableFuture<InputStream> downloadResourceFile(GetResourcesFileRequest getResourcesFileRequest) {
      return this.dispatch(getResourcesFileRequest, java.net.http.HttpResponse.BodyHandlers.ofInputStream(), HttpResponse::body);
   }

   public CompletableFuture<Void> uploadFile(FileUploadRequest fileUploadRequest) {
      return this.executeVoid(fileUploadRequest);
   }

   public CompletableFuture<Collection<ConfigComment>> getResourceComments(GetResourcesCommentsRequest getResourcesCommentsRequest) {
      Type type = (new TypeToken<LinkedList<ConfigComment>>() {}).getType();
      return this.requestJson(getResourcesCommentsRequest, type);
   }
}
