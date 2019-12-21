import java.util.concurrent.Semaphore;
import java.util.Random;

public class Customer extends Thread {

    public Random rand = new Random();

    public Customer(int i){
        setName("Customer-" + i);
    }

    public void run(){

        do {
            // After browsing once, customers will have 1/4 change to decide to go to
            browse();
        }while( (rand.nextInt(100)%4) != 1 );
        decided();
    }

    private void decided() {
        try {
            msg("Looking for floor clerk line");
            sleep(10000);
            // go on floor clerk line and wait until they are called or floor clerk is available
            MegaShowRoom.lookingForFloorClerk.acquire();

            // goes to Floor Clerk for help
            MegaShowRoom.helpCustomers.release();
            msg("Floor clerk has called me over");

            // wait for floor clerk to finish helping them
            MegaShowRoom.finishedHelpingCustomer.acquire();
            msg("Floor clerk has finished helping me");

        }catch (InterruptedException e){
            msg("INTERRUPTED on floor clerk line");
        }

        goToCashier();

    }

    private void goToCashier() {
        if(randomDecideCashOrCredit()){
            try {
                // Lets the cashier know they are on the line
                MegaShowRoom.tellCashLine.release();
                // wait for the cashier to call for checkout
                MegaShowRoom.callCustomerForCashLine.acquire();
                msg("Getting help from cashier");
                sleep(10000);
                // wait for the cashier to finish and they pay for their items
                MegaShowRoom.cashLineFinishedHelped.acquire();
            }catch (InterruptedException e){
                msg("INTERRUPTED on cashier line");
            }

        }
        else {

            try {
                // Lets the cashier know they are on the line
                MegaShowRoom.tellCreditLine.release();

                // wait for the cashier to call for checkout
                MegaShowRoom.callCreditForCashLine.acquire();
                msg("Getting help from cashier");
                sleep(10000);
                // wait for the cashier to finish and they pay for their items
                MegaShowRoom.creditLineFinishedHelped.acquire();
            }catch (InterruptedException e){
                msg("INTERRUPTED on cashier line");
            }


        }

        goToBreak();

    }

    private void goToBreak() {
        msg("I just finished with the cashier now I am going on break");
        try {
            sleep(rand.nextInt(10000));
        }catch (InterruptedException e){
            msg("INTERRUPTED on break");
        }

        // this checks if all of customers are on bus
        // before the last customer goes on the bus, they checks to see if all other customers are on the bus
        // if all other customers are on the bus, the very last customer will signals all the workers to go home
        // as well as signals all the customers, so the bus will leave
        if( (MegaShowRoom.allCustomers.getQueueLength() + 1) != MegaShowRoom.numCustomer){
            try {
                msg("Getting on the bus");
                MegaShowRoom.allCustomers.acquire();
            }catch (InterruptedException e){
                msg("Interrupted getting on bus");
            }
        }
        else{
            msg("I am the last customer, now everyone on the bus will go home!");
            for (int i = 0; i < MegaShowRoom.numCustomer - 1; i++){
                MegaShowRoom.allCustomers.release();
            }
            for(int i = 0; i < (MegaShowRoom.numCashier + MegaShowRoom.numFloorClerk); i++){
                MegaShowRoom.allWorkers.release();
            }
        }


    }

    private boolean randomDecideCashOrCredit() {

        return (rand.nextInt(100)%2 == 0);
    }

    private void browse() {
        try {
            yield();
            sleep(rand.nextInt(10000));

            // 50% chance to say "browsing..."
            // 50% chance to say "looking for items..."
            if(rand.nextInt(100)%2 == 0) {
                msg("browsing...");
            }else {
                msg("looking for items...");
            }
        }catch (InterruptedException e){
            msg("INTERRUPTED while browsing");
        }
    }


    public void msg(String m){
        System.out.println("[" + (System.currentTimeMillis()-MegaShowRoom.time )+ "]" + getName() + ": " + m);
    }
}
