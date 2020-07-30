package server;

import com.opencsv.CSVWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.logging.Logger;

public class Server {

  private final static Logger LOGGER = Logger.getLogger(Server.class.getName());
  private UserInterface userinterface;
  private EmergencyService emergencyservice;
  private boolean alerted;
  private File csvFile;
  //private UserConnection userconnection;

  public Server(UserInterface ui, EmergencyService es, DataObserver dataOb) {
    userinterface = ui;
    emergencyservice = es;
    alerted = false;
    //userconnection = new UserConnection(dataOb, new DataProcessor(this));
    //userconnection.start();
    new UserConnection(dataOb, new DataProcessor(this), this);
  }

  protected void emergency() {
    if (alerted) {
      return;
    }
    alerted = true;
    new Thread() {
      @Override
      public void run() {
        boolean userOK = userinterface.checkUser();
        if (!userOK) {
          LOGGER.info("User need Help");
          emergencyservice.callHelp();
        }
        alerted = false;
      }
    }.start();
  }

  protected void emergency(MovementData data) {
    if (alerted) {
      return;
    }
    alerted = true;
    new Thread() {
      @Override
      public void run() {
        LOGGER.info(
            "User did fall down :" + data.accX + " " + data.accY + " " + data.accZ + " " + data.posX
                + " " + data.posY + "  " + data.posZ);
        boolean userOK = userinterface.checkUser();
        if (!userOK) {
          LOGGER.info("User need Help");
          emergencyservice.callHelp();
        }
        alerted = false;
        recordDataIntoCsv(data, userOK);
      }
    }.start();
  }

  protected void createCSVFile() {
    DateTimeFormatter formatter = DateTimeFormatter.BASIC_ISO_DATE;
    String newFile = "data" + formatter.format(LocalDate.now());
    File directory = new File("src\\main\\java\\resource\\");
    int fileCount = Objects.requireNonNull(directory.list()).length;
    File file = new File("src\\main\\java\\resource\\" + newFile + fileCount + ".csv");
    try {
      if (file.createNewFile()) {
        LOGGER.info(file.getAbsolutePath() + "is created");
        CSVWriter csvWriter = new CSVWriter(new FileWriter(file.getAbsolutePath(), true));
        String now = "Time Date";
        String accX = "Accelerometer X";
        String accY = "Accelerometer Y";
        String accZ = "Accelerometer Z";
        String posX = "Gyros X";
        String posY = "Gyros Y";
        String posZ = "Gyros Z";
        String stringBuilder =
            now + "," + accX + "," + accY + "," + accZ + "," + posX + "," + posY + "," + posZ
                + ","
                + "Status";
        String[] record = stringBuilder.split(",");
        csvWriter.writeNext(record);
        csvWriter.close();
        csvFile=file;
      } else {
        LOGGER.warning(file.getAbsolutePath() + " already Exist");
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  protected void recordDataIntoCsv(MovementData data, boolean userOK) {
    if (data != null) {
      if (csvFile.exists()) {
        String absolutePath = csvFile.getAbsolutePath();
        try {
          CSVWriter csvWriter = new CSVWriter(new FileWriter(absolutePath, true));
          DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
          LocalDateTime now = LocalDateTime.now();
          String accX = String.valueOf(data.accX);
          String accY = String.valueOf(data.accY);
          String accZ = String.valueOf(data.accZ);
          String posX = String.valueOf(data.posX);
          String posY = String.valueOf(data.posY);
          String posZ = String.valueOf(data.posZ);
          String stringBuilder =
              now + "," + accX + "," + accY + "," + accZ + "," + posX + "," + posY + "," + posZ
                  + ","
                  + userOK;
          String[] record = stringBuilder.split(",");
          csvWriter.writeNext(record);
          csvWriter.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }

  protected void noNewMessage() {
    userinterface.noNewMessage();
  }

  protected void conenctionLost() {
    userinterface.connectionLost();
  }
}
