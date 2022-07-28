package de.blutmondgilde.changeloggenerator.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import de.blutmondgilde.changeloggenerator.Main;
import de.blutmondgilde.changeloggenerator.model.CFMod;
import de.blutmondgilde.changeloggenerator.model.CFModChangelog;
import de.blutmondgilde.changeloggenerator.model.CFModFile;
import de.blutmondgilde.changeloggenerator.model.CFModFileResponse;
import de.blutmondgilde.changeloggenerator.model.CFModFilesResponse;
import de.blutmondgilde.changeloggenerator.model.CFModLoader;
import de.blutmondgilde.changeloggenerator.model.CFModLoaderResponse;
import de.blutmondgilde.changeloggenerator.model.CFModResponse;
import de.blutmondgilde.changeloggenerator.model.Minecraft;
import de.blutmondgilde.changeloggenerator.model.ModFile;
import io.github.furstenheim.CopyDown;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class CurseForgeAPI {
    private static final ScheduledExecutorService executionService = Executors.newSingleThreadScheduledExecutor();
    private static final String baseURL = "https://api.curseforge.com";
    private final String token;
    private final ObjectMapper mapper = new ObjectMapper();

    public CurseForgeAPI(String token) {
        this.token = token;
        mapper.registerModule(new JodaModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

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
            }, Main.REQUEST_DELAY, TimeUnit.MILLISECONDS).get();
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
            }, Main.REQUEST_DELAY, TimeUnit.MILLISECONDS).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public CFModLoader getModLoader(Minecraft minecraft) {
        try {
            return executionService.schedule(() -> {
                try (CloseableHttpClient client = HttpClients.createDefault()) {
                    HttpGet request = authorizedGet(String.format("/v1/minecraft/modloader/%s", minecraft.getModLoaders()[0].getId()));
                    HttpEntity entity = client.execute(request).getEntity();
                    String responseString = EntityUtils.toString(entity, StandardCharsets.UTF_8);
                    CFModLoaderResponse response = mapper.readValue(responseString, CFModLoaderResponse.class);
                    return response.getData();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }, Main.REQUEST_DELAY, TimeUnit.MILLISECONDS).get();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public CFModFile getFile(ModFile file){
        try {
            return executionService.schedule(() -> {
                try (CloseableHttpClient client = HttpClients.createDefault()) {
                    HttpGet request = authorizedGet(String.format("/v1/mods/%s/files/%s", file.getProjectID(),file.getFileID()));
                    HttpEntity entity = client.execute(request).getEntity();
                    String responseString = EntityUtils.toString(entity, StandardCharsets.UTF_8);
                    CFModFileResponse response = mapper.readValue(responseString, CFModFileResponse.class);
                    client.close();
                    return response.getData();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }, Main.REQUEST_DELAY, TimeUnit.MILLISECONDS).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public List<CFModFile> getFilesBetween(CFModLoader modLoader, ModFile oldFile, ModFile newFile) {
        try {
            CFModFilesResponse modFilesResponse = executionService.schedule(() -> {
                try (CloseableHttpClient client = HttpClients.createDefault()) {
                    HttpGet request = authorizedGet(String.format("/v1/mods/%s/files?gameVersionTypeId=%s&modLoaderType=%s&pageSize=50",
                        newFile.getProjectID(),
                        modLoader.getMcGameVersionTypeId(),
                        modLoader.getType()));
                    HttpEntity entity = client.execute(request).getEntity();
                    String responseString = EntityUtils.toString(entity, StandardCharsets.UTF_8);
                    return mapper.readValue(responseString, CFModFilesResponse.class);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }, Main.REQUEST_DELAY, TimeUnit.MILLISECONDS).get();

            CFModFile oldMod = getFile(oldFile);
            CFModFile newMod = getFile(newFile);
            return Arrays.stream(modFilesResponse.getData())
                .filter(cfMod -> cfMod.getCreationDate().isAfter(oldMod.getCreationDate()))
                .filter(cfMod -> cfMod.getCreationDate().isBefore(newMod.getCreationDate()))
                .collect(Collectors.toList());
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }

    }


    private HttpGet authorizedGet(String path) {
        HttpGet request = new HttpGet(baseURL + path);
        request.addHeader("x-api-key", token);
        return request;
    }
}
