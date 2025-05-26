# Android-IP-ping-app
Android app for pinging IP addresses

## Tools used
1. Android Studio 2024.3.1
2. Android SDK API 35 (Android 15)
3. Java 11

## Instructions
Used to check if a server can be pinged from your android device.<br>
**Application**<br>
![Application](App.png) <br>

**IP List item**<br>
![IP list item](<IP List Item.png>)<br>
Each item is clickable and has the IP address, the name of the IP address and a visual que on the IP's ping status on the right side.<br>
**Loading image**<br>
![Loading image](Loading.png)<br>
This image symbolizes that the IP has not been tested. When the item is clicked the image will start to rotate and will change, once the ping is completed.<br>
**OK image**<br>
![OK image](OK.png)<br>
The green OK image is used to indicate, that the ping has been successful.<br>
**BAD image**<br>
![BAD image](BAD.png)<br>
If the ping was not successful, the bad image will be shown<br><br>
If you have many IP addresses to test, you can test all of them with one button at the top:<br>
![Play all](<Play all.png>)<br><br>
If you wish to add a new IP address, you can press the **+** button on the bottom right to call a new menu:<br>
![Add IP](<Add IP.png>)<br>

To delete a list item(s), simply hold the item to hightlited it:<br>
![Highlighted](Highlighted.png)<br>

And click on the Delete button at the top:<br>
![Delete](Delete.png)<br>

To add multiple list entries, you can use the add button at the top:<br>
![Add button](<Add multiple.png>)<br>
This button will prompt you to choose a file with the list in a JSON format. An example of the list can be found in IP List JSON.txt file. JSON string must be in one line.