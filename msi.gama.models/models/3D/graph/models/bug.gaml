model Bug

global {
    init {
        create bug number: 100;
    }
} 

environment{
}

entities {
    species bug {
        aspect basic {
            draw sphere(1) color: rgb('red');
        }
    }
}

output {
    display bug_display type:opengl{
        species bug aspect: basic;
    }
} 

