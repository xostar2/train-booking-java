package services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import entity.Train;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TrainService {

    private List<Train> trainList;
    private ObjectMapper objectMapper = new ObjectMapper();

    private static final String TRAIN_DB_PATH = "src/main/java/localDB/trains.json";

    public TrainService() throws IOException {
        File trains = new File(TRAIN_DB_PATH);
        trainList = objectMapper.readValue(trains, new TypeReference<ArrayList<Train>>() {
        });

    }

    public List<Train> searchTrains(String Source, String Destination) throws IOException {
        return trainList.stream().filter(train -> validTrain(train, Source, Destination)).collect(Collectors.toList());
    }

    public void addTrain(Train newTrain) throws IOException {
        // Check if a train with the same trainId already exists

        Optional<Train> existingTrain = trainList.stream()
                .filter(train -> train.getTrainId().equalsIgnoreCase(newTrain.getTrainId()))
                .findFirst();

        if (existingTrain.isPresent()) {
            // If a train with the same trainId exists, update it instead of adding a new one
            updateTrain(newTrain);
        } else {
            trainList.add(newTrain);
            saveTrainListToFile();
        }
    }

    public void updateTrain(Train updatedTrain) throws IOException {
        OptionalInt index = IntStream.range(0, trainList.size())
                .filter(i -> trainList.get(i).getTrainId().equalsIgnoreCase(updatedTrain.getTrainId()))
                .findFirst();

        if (index.isPresent()) {
            trainList.set(index.getAsInt(), updatedTrain);
            saveTrainListToFile();
        }

    }


    private void saveTrainListToFile() {
        try {
            objectMapper.writeValue(new File(TRAIN_DB_PATH), trainList);
        } catch (IOException e) {
            e.printStackTrace(); // Handle the exception based on your application's requirements
        }
    }

    private boolean validTrain(Train train, String source, String destination) {
        List<String> stationOrder = train.getStations();

        int sourceIndex = stationOrder.indexOf(source.toLowerCase());
        int destinationIndex = stationOrder.indexOf(destination.toLowerCase());
        return sourceIndex != -1 && destinationIndex != -1 && sourceIndex < destinationIndex;
    }

}

