# Makefile for compiling and running Java classes with packages
#Author: Kananelo Chabeli
# Define the directories
SRC_DIR = src
BIN_DIR = bin

# Define the Java compiler
JAVAC = javac

# Flags for Java compiler
JFLAGS = -d $(BIN_DIR) -sourcepath $(SRC_DIR)

# Define the packages
PACKAGES = clubSimulation


# Get all the Java source files recursively
JAVA_FILES := $(shell find $(SRC_DIR) -name "*.java")

# Convert Java source files to class files
CLASS_FILES := $(patsubst $(SRC_DIR)/%.java, $(BIN_DIR)/%.class, $(JAVA_FILES))

# Default target to compile all classes
all: $(CLASS_FILES)

# Target for compiling Java source files to class files
$(BIN_DIR)/%.class: $(SRC_DIR)/%.java
	$(JAVAC) $(JFLAGS) $<

# Target for running the MonteCarloMinimization class with command-line inputs
run: all
	java -cp $(BIN_DIR) $(PACKAGES).ClubSimulation $(filter-out $@,$(MAKECMDGOALS))

# Clean target to remove all class files
clean:
	rm -rf $(BIN_DIR)
