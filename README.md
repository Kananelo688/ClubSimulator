# ClubSimulator
ClubSimulator is an application that simulates events that happens at any club.
In recent years, concurrent programming has gained popularity in software de-
velopment because of invention of multi-core processors.Different from parallel
programming which is about running multiple tasks at the same time, concur-
rent programming describes principles of effectively managing and controlling
access to shared resources. Typical application of concurrent programming is
design and development of operating systems. An operating systems controls
all programs or processes running on a given computer and effectively allocates
resources to different processes.
A common challenge in concurrent programming is dealing with race condi-
tions. There are two forms of race conditions namely; data race and interleaving.
Data race is a race condition that occurs when different threads access same
memory location, at least one of the threads writes to the memory with no
synchronization that forces any particular order. It is important for software
developers to have deep understanding of multi-threading and measures to pre-
vent race conditions.
More often than not, prevention of race conditions is made by forcing or-
dering in the occurrence of events. This is called mutual exclusion, and process
of enforcing this ordering is called synchronization. In Java, there a number of
ways to achieve synchronization.The main purpose of this report is to document
approach and results of using synchronization mechanisms in Java.
# How to run the Program
### Step 1. Download the zip file of the repository and unzip it<br>
### step 2. Open the project folder in terminal<br>
### step 3. Run command make run N0 N1 N2 N3 , where N0 is positive integer denoting number of people expected to come to club, N1 is number of x-axis grids, N2 is the number of y-axis grids and N3 in the maximum number of patrons allowed in a club at any given time ( N3<N0).
