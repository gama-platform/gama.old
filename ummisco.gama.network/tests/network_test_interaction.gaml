/***
* Name: network test
* Author: Nicolas M.
* Description: 
* Tags: Network
***/

model networktest

species Person skills:[network]
{
	init
	{	}
}

experiment "Tests for ummisco.gama.network" type: test skills:[network] {
	bool already_build <- false;
	setup
	{
		if(!already_build)
		{
			create Person with:[name::"Travis"]
			{
				do connect with_name:name force_network_use:true;
			}	
			create Person with:[name::"Glory"]
			{
				do connect with_name:name;
				write name;
			}
			create Person with:[name::"Linda"]
			{
				do connect with_name:name;
			}
			create Person with:[name::"Rose"]
			{
				do connect with_name:name;
			}
			create Person with:[name::"Julia"]
			{
				do connect with_name:name;
			}
			
			already_build <- true;	
		}
		ask Person
		{
			loop while:self.has_more_message()
			{
				message mm<- fetch_message();	
			}
		}
	}

	test "Message sending" {
		bool test <- false;
		try{ 
			ask Person where(each.name = "Travis") {
				do send to:"Glory" contents:"Travis is the best tester of the World and of the Universe";
				 test<-true;
			}
		} catch {
			 test<-false;
		} 
		write "Message sending -- done!";
		assert test;
	}
	
	test "Message receiving" {
		bool test <- false;
		ask Person where(each.name = "Travis") {
				do send to:"Rose" contents:"Travis is the best tester of the World and of the Universe";
				 test<-true;
			}
		float mtime <- machine_time;
		loop while: ((mtime+1000) > machine_time){ }
		ask Person where(each.name = "Rose")
		{
			do simulate_step;
			if(length(mailbox)>0) {test<-true;}
				else {test<-false;}
		}
		write "Message receiving -- done!";
		assert test;	 
	}

	test "Message contents" {
		bool test <- false;
		ask Person where(each.name = "Travis") {
				do send to:"Linda" contents:"Travis is the best tester of the World and of the Universe";
			}
		float mtime <- machine_time;
		loop while: ((mtime+1000) > machine_time){ }
		ask Person where(each.name = "Linda")
		{
			do simulate_step;
			list<message> msg <- mailbox;
			if(first(msg).contents = "Travis is the best tester of the World and of the Universe") {
				
				test<-true;
			}
				else {test<-false;}
		}
		write "Message contents -- done!";
		assert test;	 
	}
	
	test "has_more_element and fetch_message action" {
		bool a1; 
		bool a2;
		bool a3;
		bool a4;
		ask Person where(each.name = "Travis") {
				do send to:"Julia" contents:"Travis is the best tester of the World and of the Universe";
			}
		float mtime <- machine_time;
		loop while: ((mtime+2000) > machine_time){ }
		ask Person where(each.name = "Julia")
		{
			do simulate_step;
			a1 <- length(mailbox)=1;
			a2 <- has_more_message();
			a3 <- first(mailbox) = fetch_message();
			a4 <- nil = fetch_message();
		}
		
		write "has_more_element and fetch_message action -- done!";
		assert (a1 and a2 and a3 and a4);
	}
	
	test "network group" {
		bool a1; 
		bool a2;
		bool a3;
		bool a4;
		
		bool p1;
		bool p2;
		bool p3;
		bool p4;
		ask Person where(each.name = "Travis") {
				do send to:"ALL" contents:"Travis is the best tester of the World and of the Universe";
			}
		float mtime <- machine_time;
		loop while: ((mtime+2000) > machine_time){ }
		ask Person where(each.name = "Julia")
		{
			do simulate_step;
			a1 <- length(mailbox)=1;
			a2 <- has_more_message();
			a3 <- first(mailbox) = fetch_message();
			a4 <- nil = fetch_message();
		}
		p1 <- (a1 and a2 and a3 and a4);
		ask Person where(each.name = "Glory")
		{
			a1 <- length(mailbox)=1;
			a2 <- has_more_message();
			a3 <- first(mailbox) = fetch_message();
			a4 <- nil = fetch_message();
		}
		p2 <- (a1 and a2 and a3 and a4);
		ask Person where(each.name = "Rose")
		{
			a1 <- length(mailbox)=1;
			a2 <- has_more_message();
			a3 <- first(mailbox) = fetch_message();
			a4 <- nil = fetch_message();
		}
		p3 <- (a1 and a2 and a3 and a4);
		ask Person where(each.name = "Travis")
		{
			do simulate_step;
			a1 <- length(mailbox)=1;
			a2 <- has_more_message();
			a3 <- first(mailbox) = fetch_message();
			a4 <- nil = fetch_message();
		}
		p4 <- (a1 and a2 and a3 and a4);
		
		
		write "network group -- done!";
		assert (p1 and p2 and p3 and p4);
	}
	
	
	
}