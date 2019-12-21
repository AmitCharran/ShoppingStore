import java.util.Random;
import java.util.concurrent.Semaphore;


public class Cashier extends Thread {

    private boolean cash;
    public Random rand = new Random();
    static int customersHelped = 0;

    public Cashier(boolean c){
        cash = c;
        if(cash == true){
            setName("Cash");
            msg("I am a cash cashier and I am ready to work");
        }
        else {
            setName("Credit");
            msg("I am a credit cashier and I am ready to work");
        }
    }

    public void run(){
        if(cash == true){
            msg("I am a cash cashier and I am ready to work");
        }
        else {
            msg("I am a credit cashier and I am ready to work");
        }
        while(true){
            try {
                if (customersHelped == MegaShowRoom.numCustomer){
                    msg("I have finished helping customers now");

                    for(int i = 0; i < MegaShowRoom.numCashier; i++) {
                        // release cashier if they are waiting on customer
                        MegaShowRoom.tellCreditLine.release();
                        MegaShowRoom.tellCashLine.release();
                    }

                    // waits until all customers leave before they can leave
                    msg("Waiting for closing time");
                    MegaShowRoom.allWorkers.acquire();
                    msg("Now I can go home");

                    break;
                }


                if(cash) {
                    // the code customerHelped < MegaShowRoom.numCustomer is the same as in floor clerk
                    // if all customers are served by pass semaphore and go straight to waiting for job closing
                    if (customersHelped < MegaShowRoom.numCustomer) {
                        // wait for customer to go into the line
                        MegaShowRoom.tellCashLine.acquire();
                        msg("I am going to help customer in line now");
                    }
                    if (customersHelped < MegaShowRoom.numCustomer) {
                        // call customer from the line and help them
                        MegaShowRoom.callCustomerForCashLine.release();
                        msg("Helping customer");
                        sleep(rand.nextInt(10000));
                    }

                    if (customersHelped < MegaShowRoom.numCustomer) {
                        // let the customer go after they pay
                        sleep(rand.nextInt(10000));
                        MegaShowRoom.cashLineFinishedHelped.release();
                    }



                }
                else{
                    if (customersHelped < MegaShowRoom.numCustomer) {
                        // wait for customer to go into the line
                        MegaShowRoom.tellCreditLine.acquire();
                        msg("I am going to help customer in line now");
                    }
                    if (customersHelped < MegaShowRoom.numCustomer) {
                        // call customer from the line and help them
                        MegaShowRoom.callCreditForCashLine.release();
                        msg("Helping customer");
                        sleep(rand.nextInt(10000));
                    }
                    if (customersHelped < MegaShowRoom.numCustomer) {
                        // let the customer go after they pay
                        sleep(rand.nextInt(10000));
                        MegaShowRoom.creditLineFinishedHelped.release();
                    }
                }

                // use the MUTEX for the counter, and increment the customers who has been helped
                if(customersHelped < MegaShowRoom.numCustomer) {
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
        System.out.println("[" + (System.currentTimeMillis()-MegaShowRoom.time ) + "]" + getName() + ": " + m);


    }

}
