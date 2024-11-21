package dao;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobClientBuilder;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobContainerClientBuilder;
import javafx.concurrent.Task;
import javafx.scene.control.ProgressBar;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.nio.file.Files;

public class StorageUploader {

    //part 1
    private BlobContainerClient containerClient;

    public StorageUploader( ) {
        this.containerClient = new BlobContainerClientBuilder()
                .connectionString("DefaultEndpointsProtocol=https;AccountName=XXXXXXXXXXX;AccountKey=XXXXXXXXoNx2TQroaPQzcXiqPNBm0TimZ+EQHd5vwS1HhYJt9aPmElI8+l4++ASty+glzA==;EndpointSuffix=core.windows.net")
                //connection string
                //DefaultEndpointsProtocol=https;AccountName=kaurcsc311storage;AccountKey=zwCzoO+p4vCgSfPwt9oj1tEsYlPJX9jabu1H1J4GQskYW8MTCW1x+inXGgrZrBKCpaxKOlmQBMkI+AStURBjGA==;EndpointSuffix=core.windows.net
                .containerName("media-files")
                .buildClient();
    }

    public void uploadFile(String filePath, String blobName) {
        BlobClient blobClient = containerClient.getBlobClient(blobName);
        blobClient.uploadFromFile(filePath);
    }
    public BlobContainerClient getContainerClient(){
        return containerClient;
    }


}
