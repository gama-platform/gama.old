/***
* Name: networktest
* Author: nicolas
* Description: 
* Tags: Tag1, Tag2, TagN
***/

model networktest

species Person skills:[network]
{
	init
	{
		
	}
}

experiment "Tests for ummisco.gama.network" type: test skills:[network] {
	bool already_build <- false;
	setup
	{
		if(!already_build)
		{
			create Person with:[name::"Travis"]
			{
				do connect with_name:name;
			}	
			create Person with:[name::"Glory"]
			{
				do connect with_name:name;
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
		assert (a1 and a2 and a3 and a4);
	}
	
}