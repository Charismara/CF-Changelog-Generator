package de.blutmondgilde.changeloggenerator.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.blutmondgilde.changeloggenerator.model.CFMod;
import de.blutmondgilde.changeloggenerator.model.CFModChangelog;
import de.blutmondgilde.changeloggenerator.model.CFModResponse;
import de.blutmondgilde.changeloggenerator.model.ModFile;
import io.github.furstenheim.CopyDown;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class CurseForgeAPI {
    private static final ScheduledExecutorService executionService = Executors.newSingleThreadScheduledExecutor();
    private static final String baseURL = "https://api.curseforge.com";
    private final String token;
    private final ObjectMapper mapper = new ObjectMapper();

    public CFMod getModInformation(ModFile file) {
        try {
            return executionService.schedule(() -> {
                try (CloseableHttpClient client = HttpClients.createDefault()) {
                    HttpGet request = authorizedGet(String.format("/v1/mods/%s", file.getProjectID()));
                    HttpEntity entity = client.execute(request).getEntity();
                    String responseString = EntityUtils.toString(entity, StandardCharsets.UTF_8);
                    CFModResponse response = mapper.readValue(responseString, CFModResponse.class);
                    client.close();
                    return response.getData();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }, 50, TimeUnit.MILLISECONDS).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public String getModChangeLog(ModFile file) {
        try {
            return executionService.schedule(() -> {
                try (CloseableHttpClient client = HttpClients.createDefault()) {
                    HttpGet request = authorizedGet(String.format("/v1/mods/%s/files/%s/changelog", file.getProjectID(), file.getFileID()));
                    HttpEntity entity = client.execute(request).getEntity();
                    String responseString = EntityUtils.toString(entity, StandardCharsets.UTF_8);
                    CFModChangelog response = mapper.readValue(responseString, CFModChangelog.class);
                    client.close();
                    String result = response.getData();
                    CopyDown converter = new CopyDown();
                    result = converter.convert(result);
                    result = result.replace("\\", "");
                    return result;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }, 50, TimeUnit.MILLISECONDS).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }


    private HttpGet authorizedGet(String path) {
        HttpGet request = new HttpGet(baseURL + path);
        request.addHeader("x-api-key", token);
        return request;
    }
}
