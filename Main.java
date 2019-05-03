// package
package maintype;

// imports
import java.io.*;
import java.util.*;

// main class
public class Main {
	
	static Bank b = new Bank( new ArrayList<CreditCard>() );
	static Customer[] custAccounts;
	static Supplier[] supAccounts;
	static Customer activeCustomer;
	static Supplier activeSupplier;
	static boolean customerFlag;
	static Scanner input = new Scanner(System.in);
	static ArrayList<Item> catalog = new ArrayList<Item>();
	static String filePath = new File("").getAbsolutePath();
	
	private static boolean login() {
		
		// Get user input
		System.out.println("\n\nAre you a customer? (yes/no)");
		String customer = input.nextLine();
		customerFlag = customer.equalsIgnoreCase("yes");
		
		System.out.println("\n ---------------- Please Login ----------------- \n" + "Username: \n");
        String username = input.nextLine();
        System.out.println("Password: \n");
        String password = input.nextLine();
        
        // Check if file exists, if not create it
        
        File accountFile = new File(filePath + "/src/main/accounts/" + username + ".txt");
        if(accountFile.exists()) {
        	
        	try {
	        	// read from Account.txt to see if account information exists
		        BufferedReader Buff = new BufferedReader(new FileReader(accountFile));
		        String ID = Buff.readLine();
		        String NAME = Buff.readLine();
		        String PASSWORD = Buff.readLine();
		        
		        if(!PASSWORD.equals(password)) {
		        	System.out.println("\n\n\nIncorrect Password.\n\n");
		        	return false;
		        }
		    
		        if(customerFlag) {
		        	String ADDRESS = Buff.readLine();
		 	        String PHONE = Buff.readLine();
		 	        String C_NUMBER = Buff.readLine();
		 	        String C_CVV = Buff.readLine();
		 	        String C_EXPDATE = Buff.readLine();
		 	        String C_LIMIT = Buff.readLine();
		 	        
		 	        activeCustomer = new Customer(Integer.parseInt(ID), NAME, PASSWORD, ADDRESS, PHONE, new CreditCard(C_NUMBER, Integer.parseInt(C_CVV), C_EXPDATE, Integer.parseInt(C_LIMIT)));
		        } else {
		        	activeSupplier = new Supplier(Integer.parseInt(ID), NAME, PASSWORD);
		        }
	
	        	System.out.println("\nYou've successfully logged in.\n");
        	} catch(Exception e) {
        		e.printStackTrace();
        		return false;
        	}
        	
        } else {
        	
        	createAccount(username);
        	
        }
        
        return true;
		
	}
	
	private static void createAccount(String username) {
		      
	   	System.out.print("We could not find a profile with your information. Would you like to create an account? (yes/no)");
		String decision = input.nextLine();

		switch(decision){
			case "yes":
				String password, address, phone = null, c_number = null, c_cvv = null, c_expDate = null, c_limit = null;
	            System.out.print("Please enter Password: ");
	            password = input.nextLine();
	            System.out.print("Please enter Address: ");
	            address = input.nextLine();
	            if(customerFlag) {
	            	System.out.print("Please enter Phone Number: ((xxx)xxx-xxxx) ");
		            phone = input.nextLine();
		            System.out.println("\nNow we will add your credit card.\n");
		            System.out.print("Please enter Credit Card Number: (xxxx-xxxx-xxxx-xxxx) ");
		            c_number = input.nextLine();
		            System.out.print("Please enter Card Security Code: (xxx) ");
		            c_cvv = input.nextLine();
		            System.out.print("Please enter Card Expiration Date: (mm/yy) ");
		            c_expDate = input.nextLine();
		            System.out.print("Please enter Credit Limit: (xxxxx) ");
		            c_limit = input.nextLine(); 
	            }    
		            
	            FileWriter fWriter = null;
	            BufferedWriter writer = null;
	            try {
	            	int id = new File(filePath + "/src/main/accounts").list().length + 1;
	            	fWriter = new FileWriter(filePath + "/src/main/accounts/" + username + ".txt");
	            	writer = new BufferedWriter(fWriter);
	              
	            	if(customerFlag) {
	            		writer.write(id+"\r\n"+username+"\r\n"+password+"\r\n"+address+"\r\n"+phone+"\r\n"+c_number+
	            		  "\r\n"+c_cvv+"\r\n"+c_expDate+"\r\n"+c_limit);
	            		System.err.println("Customer account successfully created!");
	            	} else {
	            		writer.write(id+"\r\n"+username+"\r\n"+password);
	            		System.err.println("Supplier account successfully created!");
	            	}
	            	writer.newLine();
	            	writer.close();
	            	
	            	if(customerFlag) {
	            		CreditCard custCard =  new CreditCard(c_number, Integer.parseInt(c_cvv), c_expDate, Integer.parseInt(c_limit));
	            		activeCustomer = new Customer(id, username, password, address, phone, custCard);
	            		b.addCard(custCard);
	            		
	            	} else {
	            		activeSupplier = new Supplier(id, username, password);
	            	}

	            } catch (Exception e) {
	                e.printStackTrace();
	            }
	        break;
		    default: 
		    	System.err.println("\nLogging out...");
		    	System.exit(0);
		}
		
	}
	
	private static void browse() {
			
	 	System.out.println("****************************************************");
	 	System.out.println("Welcome to the catalog");

	    double total = 0;
	    boolean browsing = true;
	    while(browsing) {
	    	
	    	System.out.println("Select an item from the browsing catalog");
		 	System.out.println("-----------------------------------------------------");
	    	
	    	for(Item item : catalog)
		 		System.out.println((item.id + 10) + ") " + item.name + ": " + item.formattedPrice());
	    	
	    	int selection = input.nextInt();
		    
		    System.out.println("How many " + catalog.get(selection-11).name + "(s) would you like?");
		    
		    int quantity = input.nextInt();
		    
		    // Add selection * 
		    for(int i = 0; i < quantity; i++) {
		    	activeCustomer.items.add(catalog.get(selection-11));
		    	total += catalog.get(selection-11).price;
		    }
		    
		    System.out.println("Added " + quantity + " " + catalog.get(selection-11).name + "(s) to your cart.");
		    System.out.println("\nYour new total is $" + (int)(total * 100)/100 + "." + (int)(total*100)%100 + "\n\n");
		    
		    System.out.println("Would you like to continue browsing? (yes/no)");
		    input.nextLine();
		    String sel = input.nextLine();
		    if(!sel.equals("yes")) browsing = false;
	    	
	    }
        
	}
	
	private static void checkout() {
		
		System.out.println("Here is your cart:\n");
		
		double total = 0.0;
		for(Item item : activeCustomer.items) {
			total += item.price;
			System.out.println("    " + item.name + "     " + item.formattedPrice());
		}
		
		System.out.println("\nTotal: $" + (int)(total * 100)/100 + "." + (int)(total*100)%100);
		
		System.out.println("\n\nWould you like to purchase these items? (yes/no)");
		input.nextLine();
		String sel = input.nextLine();
		
		if(sel.equals("yes")) {
			System.out.println("Please confirm your credit card's CVV: ");
			int cvv = input.nextInt();
			System.out.println("Please confirm your credit card's expiration date: (mm/yy)");
			input.nextLine();
			String expDate = input.nextLine();
			if(activeCustomer.creditCard.validate(cvv, expDate)) {
				activeCustomer.makeOrderRequest(b, cvv, expDate);
			} else {
				System.err.println("Your credit card was not authorized for this purchase.");
			}
		}
		
	}

	public static void main(String[] args) {
		
		// populate catalog
		catalog.add(new Item(1, "iPhone 7", 449.99));
		catalog.add(new Item(2, "iPhone 8", 599.99));
		catalog.add(new Item(3, "iPhone XR", 749.99));
		catalog.add(new Item(4, "iPhone XS", 999.99));
		catalog.add(new Item(5, "iPad Pro", 799.99));
		catalog.add(new Item(6, "iPad Air", 499.99));
		catalog.add(new Item(7, "iPad", 329.99));
		catalog.add(new Item(8, "iPad mini", 399.99));
		catalog.add(new Item(9, "Apple Pencil", 99.99));
		catalog.add(new Item(10, "Apple Pencil (2nd Gen)", 129.99));
		catalog.add(new Item(11, "AirPods", 199.99));
		
		// populate bank
		b.addCard(new CreditCard("1111-2222-3333-4444", 000, "01/21", 100000));
		b.addCard(new CreditCard("1111-1111-1111-1111", 123, "08/29", 40000));

		// welcome
		System.out.println("--- Welcome to Online Shopping System ---");
		while(true) {
		
			if(!login()) return;
			
			int sel = 0;
			// main functions
			if(customerFlag) {
				while(sel != 4) {
					System.out.println("\n\n 1) Browse Catalog \n 2) View Your Orders \n 3) Checkout \n 4) Log Out");
					sel = input.nextInt();
					switch(sel) {
						case 1: 
							browse();
						break;
						
						case 2:
							activeCustomer.viewOrders();
						break;
						
						case 3:
							checkout();
						break;
						
						case 4:
							System.err.println("\nLogging out...");
						break;
						
						default:
							System.err.println("\nNot an option.");
					}
				}
			} else {
				while(sel != 3) {
					System.out.println("\n\n\n\n --- Supplier Portal --- \n 1) Process Delivery Orders \n 2) Confirm Shipments \n 3) Log Out");
					sel = input.nextInt();
					switch(sel) {
						case 1: 
							activeSupplier.processDeliveryOrder();
						break;
						
						case 2:
							activeSupplier.confirmShipment();
						break;
						
						case 3:
							System.err.println("\nLogging out...");
						break;
						
						default:
							System.err.println("\nNot an option.");
					}
				}
			}
			
		}
			
	}
}
