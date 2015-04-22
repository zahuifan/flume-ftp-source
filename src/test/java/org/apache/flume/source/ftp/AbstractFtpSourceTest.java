package org.apache.flume.source.ftp;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.apache.flume.Event;
import org.apache.flume.Context;
import org.apache.flume.channel.ChannelProcessor;
import static org.mockito.Mockito.*;

import org.apache.flume.source.Source;
import org.apache.flume.metrics.FtpSourceCounter;
import org.apache.flume.source.TestFileUtils;
import org.apache.flume.source.ftp.server.EmbeddedFTPServer;
import org.apache.log4j.Logger;
import org.mockito.MockitoAnnotations;
import org.mockito.Mock;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

public abstract class AbstractFtpSourceTest extends EmbeddedFTPServer{
    private Logger logger = Logger.getLogger(getClass());

    @Mock
    Context mockContext = new Context();

    Source ftpSource;
    FtpSourceCounter ftpSourceCounter;

    int getPort = 2121;

    String getUser = "flumetest";
    String getPassword = "flumetest";
    String getHost = "localhost";
    String getWorkingDirectory = null;
    String getFileName = "hasmap.ser";
    String getFolder = "/var/tmp";
    String getAbsoutePath = "/var/tmp/hasmap.ser";
    String getSource = "ftp";
    
    

    @BeforeMethod
    public void beforeMethod() {
        MockitoAnnotations.initMocks(this);

        when(mockContext.getString("client.source")).thenReturn(getSource);
        when(mockContext.getInteger("buffer.size")).thenReturn(0);
        when(mockContext.getString("name.server")).thenReturn(getHost);
        when(mockContext.getString("user")).thenReturn(getUser);
        when(mockContext.getString("password")).thenReturn(getPassword);
        when(mockContext.getInteger("run.discover.delay")).thenReturn(100);
        when(mockContext.getInteger("port")).thenReturn(getPort);
        when(mockContext.getString("working.directory")).thenReturn(getWorkingDirectory);
        when(mockContext.getString("file.name")).thenReturn(getFileName);
        when(mockContext.getString("folder")).thenReturn(getFolder);
       
        

        logger.info("Creating FTP source");

        ftpSource = new Source();
        ftpSource.configure(mockContext);
        ftpSourceCounter = new FtpSourceCounter("SOURCE.");
        ftpSource.setFtpSourceCounter(ftpSourceCounter);

        class DummyChannelProcessor extends ChannelProcessor {
            public DummyChannelProcessor() {
                super(null);
            }
            @Override
            public void processEvent(Event event) {}
        }

        ftpSource.setChannelProcessor(new DummyChannelProcessor());
    }

    @AfterMethod
    public void afterMethod() {
        try {
            logger.info("Stopping FTP source");
            ftpSource.stop();

            Paths.get(getFileName).toFile().delete();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void cleanup(Path file) {
        try {
            TestFileUtils.forceDelete(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void cleanup(List<Path> files) {
        for (Path f : files) {
            try {
                TestFileUtils.forceDelete(f);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
