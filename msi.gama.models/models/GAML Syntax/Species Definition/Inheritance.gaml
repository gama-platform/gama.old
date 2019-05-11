/**
* Name: Inheritance
* Author: Alexis Drogoul
* Description:  A simple abstract model to show the usage of inheritance
* Tags: GAML
*/

model Inheritance

global {
	init {
		create child {
			do init(10,10);
		}
		ask child {
			do add;
		}
	}
}

species parent {
	int a; 
	int b;
	
	action init (int va, int vb) {
		self.a <- va;
		self.b <- vb;
	}

	int add {
		do do_nothing();
		write do_something();
		return a + b;
	}
	
	action do_nothing virtual: true; // do_nothing is abstract and cannot be called. In addition it makes parent an abstract species
	action do_something virtual: true type:bool;// do_nothing is abstract and cannot be called. In addition it makes parent an abstract species
}

species child parent: parent {
	action init(int va, int vb) {
		invoke init(va + 20, vb+20); // we invoke the super implementation of init with the keyword invoke, if the action is not used as a function
	}
	
	int add {
		int result <- super.add(); // when the action is used as a function, we invoke the super implementation of add with the keyword super
		write result;
		write do_something();
		return result;
	}
	bool do_something{return true;}
	action do_nothing {write "nothing";} // virtual actions inherited from the parent must be redeclared or the species will be considered as abstract
}

experiment Run {}

