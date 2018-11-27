package at.sbaresearch.microg.adapter.sample;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface BackendRestClient {

  @POST("register")
  Call<Void> sendRegistrationId(@Body String registrationId);

}
