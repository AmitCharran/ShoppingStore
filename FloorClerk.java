import java.util.Random;
import java.util.concurrent.Semaphore;

public class FloorClerk extends Thread {

    static int customersHelped = 0;


    public Random rand = new Random();

    public FloorClerk(int i){
        setName("FloorClerk-" + i);

    }

    public void run(){
        msg("I am a floor clerk and I and ready to work");
        while(true){

            // They will signal a customer to help them or they will make themselves available to help
            MegaShowRoom.lookingForFloorClerk.release();

            try {

                // This code lets the floor clerk go home
                if(customersHelped == MegaShowRoom.numCustomer){
                    for(int i = 0; i < MegaShowRoom.numFloorClerk; i++) {
                        MegaShowRoom.helpCustomers.release();
                    }
                    msg("Waiting for closing time");

                    // Here they wait for all other workers to finish working
                    // The last customer will release them to go home
                    MegaShowRoom.allWorkers.acquire();
                    msg("Now I can leave work");
                    sleep(10000);
                    // takes them out of the loop so they can complete their process
                    break;
                }


                // the code customerHelped < MegaShowRoom.numCustomer basically means
                // if all customers are served by pass semaphore and go straight to waiting for job closing
                if (customersHelped < MegaShowRoom.numCustomer) {

                    // Wait for customers to signal them, so they can help the customer
                    MegaShowRoom.helpCustomers.acquire();
                    msg("Helping a customer now");
                    sleep(rand.nextInt(10000));
                }



                if (customersHelped < MegaShowRoom.numCustomer) {
                    // finished helping the customer, so release them to go to the cashier
                    MegaShowRoom.finishedHelpingCustomer.release();
                    msg("Finished helping a customer");
                }

                // use the MUTEX for the counter, and increment the customers who has been helped
                if (customersHelped < MegaShowRoom.numCustomer) {
                    MegaShowRoom.MUTEX.acquire();
                    customersHelped++;
                    MegaShowRoom.MUTEX.release();
                }
            }catch (InterruptedException e){
                msg("INTERRUPTED");
            }
        }

    }



    public void msg(String m){
        System.out.println("[" +(System.currentTimeMillis()-MegaShowRoom.time ) + "]" + getName() + ": " + m);
    }
}
