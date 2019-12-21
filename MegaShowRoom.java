import java.util.concurrent.Semaphore;
// Amit Charran
// Implementing concurrency using threads and semaphores

public class MegaShowRoom {

    public static long time = System.currentTimeMillis();

    // For Floor Clerk / Customer use
    public static Semaphore lookingForFloorClerk = new Semaphore(0,true);
    // ^ This (lookingForFloorClerk) is for customers to wait until floor clerks are available.

    public static Semaphore helpCustomers= new Semaphore(0, true);
    // ^(helpCustomers) floor clerk will now help customers.

    public static Semaphore finishedHelpingCustomer = new Semaphore(0,true);
    // ^ (finishedHelpingCustomers) Customers will wait until Floor Clerk finishes helping them.
    // When floor clerk releases customers, then they will go to the cashier





    // For Cashier / Customer use
    public static Semaphore tellCashLine = new Semaphore(0, true);
    // ^ (tellCashLine) initially the cash cashier will wait on this semaphore
    //   Then the customer will signal the cash cashier
    public static Semaphore callCustomerForCashLine = new Semaphore(0, true);
    // ^ (callCustomerForCashLine) customer waits on this semaphore
    //   cashier will call them when they are ready
    public static Semaphore cashLineFinishedHelped = new Semaphore(0, true);
    // ^ (cashLineFinishedHelped) cashier will help customers then release them

    public static Semaphore tellCreditLine = new Semaphore(0, true);
    // ^(tellCreditLine) same as tellCashLine
    public static Semaphore callCreditForCashLine = new Semaphore(0, true);
    // ^(callCreditForCashLine) same as callCustomerForCashLine
    public static Semaphore creditLineFinishedHelped = new Semaphore(0, true);
    // ^(creditLineFinishedHelped) same as cashLineFinishedHelped



    // For Floor Clerk, Customer, and Cashier use
    public static Semaphore allWorkers = new Semaphore(0, true);
    // ^This (allWorkers) is for Floors Clerks and Cashier to wait for the bus to close
    // The very last customer will release them to go home

    // For Customer Use
    public static Semaphore allCustomers = new Semaphore(0, true);
    // ^ This(allCustomers) is for customers when they wait on the bus
    //  The last customer will release all other customers, so the bus can leave

    // For Floor Clerk and Cashier counters
    public static Semaphore MUTEX = new Semaphore(0, true);
    // ^ This(MUTEX) is for floor clerk and cashier to count how many customers they've helped




    public static int numCustomer;
    public static int numFloorClerk;
    public static int numCashier;


    public static void main(String argv[]){

        // initialize MUTEX to 1
        // So now I can treat it like a Binary Semaphore initialize to 1
        MUTEX.release();

        String numberCustomer = argv[0];

        try{
            numCustomer = Integer.parseInt(numberCustomer);
        }catch (NumberFormatException nfe){
            System.out.println("Input is not Integer: Number Customer default to 12");
            numCustomer = 12;
        }

        numCashier = 2;
        numFloorClerk = 3;

        Cashier[] allCashiers = new Cashier[numCashier];
        FloorClerk[] allFloorClerk = new FloorClerk[numFloorClerk];
        Customer[] allCustomers = new Customer[numCustomer];

        for(int i = 0; i < numCashier; i++){
            allCashiers[i] = new Cashier((i%2)==0);
        }
        for (int i = 0; i < numFloorClerk; i++){
            allFloorClerk[i] = new FloorClerk(i + 1);
        }
        for (int i = 0; i < numCustomer; i++){
            allCustomers[i] = new Customer(i + 1);
        }

        for(int i = 0; i < numCashier; i++){ ;
            allCashiers[i].start();
        }
        for (int i = 0; i < numFloorClerk; i++){
            allFloorClerk[i].start();
        }
        for (int i = 0; i < numCustomer; i++){
            allCustomers[i].start();
        }

    }

}
