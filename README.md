# Magnus - Mobile Application

Magnus is a Java Application developed for android and is one of a minimum of three required components. For operation, Magnus - Server must also be running alongside an up-to-date Database to allow application to verify actions.

## Installation - Required Software
 
XAMPP with MySQL  
Microsoft Visual Studio 2017 -> 2019  
Android Studio 3.5 Initial Public Release (https://developer.android.com/studio/archive \[August 20, 2019])  

## Usage - Required Components
 
Magnus - Mobile Application  
Magnus - Server (https://gitlab.com/segfault3801/magnusserver)  
MySQL Database (https://gitlab.com/segfault3801/magnusserver/-/blob/master/Database%20Injection)  
SmartPhone at Android 21.0 (Lollipop) or above with ADB enabled or an Intel-based machine with virtualisation enabled  

1. Start `Apache` and `MySQL` modules in XAMPP
2. Set up database (See Below)
3. Run Program.cs in MagnusServer/Magnus
4. Install Magnus Mobile Application on desired suitable device through Android Studio
5. Open Application on device and begin - Registration in-app is required

## Database Setup

1. Head to http://localhost/phpmyadmin
2. Create New Table with Name `deco7381_build`
3. Go to SQL tab
4. Paste `Database Injection` MySQL Queries
5. Press Go

## Authors

@Subzerofusion  
@Lyxaa  
@markbirdy92  