package services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import entity.Train;
import entity.User;
import util.UserServicesUtil;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;


public class UserBookingServices {

    private User user;
    private List<User>userList;
    private ObjectMapper objectMapper = new ObjectMapper();

    private static final String USER_PATH="src/main/java/localDb/users.json";


    private static final String TRAIN_PATH="src/main/java/localDB/trains.Json";
    public UserBookingServices(User user)  throws IOException {
        this.user = user;
        LoadUser();

    }
    public UserBookingServices () throws IOException {
        LoadUser();
    }

    public List<User>LoadUser() throws IOException{
        File users= new File(USER_PATH);
        //this is important part typerefernce
       return  userList= objectMapper.readValue(users, new TypeReference<List<User>>() {});
    }

    public Boolean loginUser (){
        Optional<User> foundUser = userList.stream().filter(user1->{
            return user1.getName().equalsIgnoreCase(user.getName()) &&
            UserServicesUtil.checkPassword(user.getPassword(),user1.getHashedPassword());
        }).findFirst();
        //findFirst= give me the first element you found in the list
        return foundUser.isPresent();
    }

    public Boolean SignUp(User user){
        try{
            userList.add(user);
            saveUserListToFile();
            return Boolean.TRUE;
        }catch(Exception e){
           return Boolean.FALSE;
        }
    }

    private void saveUserListToFile() throws IOException {
        File usersFile = new File(USER_PATH);
//        objectMapper = new ObjectMapper();
        objectMapper.writeValue(usersFile,userList);
    }

     public void fetchBooking(){
         Optional<User> userFetched = userList.stream().filter(user1 -> {
             return user1.getName().equals(user.getName()) && UserServicesUtil.checkPassword(user.getPassword(), user1.getHashedPassword());
         }).findFirst();
         if(userFetched.isPresent()){
             userFetched.get().printTickets();
         }
     }
    public Boolean cancelBooking(String ticketId){

        Scanner s = new Scanner(System.in);
        System.out.println("Enter the ticket id to cancel");
        ticketId = s.next();

        if (ticketId == null || ticketId.isEmpty()) {
            System.out.println("Ticket ID cannot be null or empty.");
            return Boolean.FALSE;
        }

        String finalTicketId1 = ticketId;  //Because strings are immutable
        boolean removed = user.getTicketsBooked().removeIf(ticket -> ticket.getTicketId().equals(finalTicketId1));

        String finalTicketId = ticketId;
        user.getTicketsBooked().removeIf(Ticket -> Ticket.getTicketId().equals(finalTicketId));
        if (removed) {
            System.out.println("Ticket with ID " + ticketId + " has been canceled.");
            return Boolean.TRUE;
        }else{
            System.out.println("No ticket found with ID " + ticketId);
            return Boolean.FALSE;
        }
    }


    public List<Train> getTrains(String source, String destination){
        try{
            TrainService trainService = new TrainService();
            return trainService.searchTrains(source, destination);
        }catch(IOException ex){
            return new ArrayList<>();
        }
    }

    public List<List<Integer>> fetchSeats(Train train){
        return train.getSeats();
    }

    public Boolean bookTrainSeat(Train train, int row, int seat) {
        try{
            TrainService trainService = new TrainService();
            List<List<Integer>> seats = train.getSeats();
            if (row >= 0 && row < seats.size() && seat >= 0 && seat < seats.get(row).size()) {
                if (seats.get(row).get(seat) == 0) {
                    seats.get(row).set(seat, 1);
                    train.setSeats(seats);
                    trainService.addTrain(train);
                    return true; // Booking successful
                } else {
                    return false; // Seat is already booked
                }
            } else {
                return false; // Invalid row or seat index
            }
        }catch (IOException ex){
            return Boolean.FALSE;
        }
    }

}
