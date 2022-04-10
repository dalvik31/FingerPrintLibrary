# FingerPrintLibrary

> Step 1. Add the JitPack repository to your build file

```gradle
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
  ```
  
  
> Step 2. Add the dependency

```
	dependencies {
	        implementation 'com.github.dalvik31:FingerPrintLibrary:Tag'
	}
  ```
 > Step 3. Use

```
inicializate:
 val managerFingerPrint: ManagerFingerPrint = ManagerFingerPrint.from(this)
 
 use:
 button.setOnClickListener {
            managerFingerPrint.initAuthentication { isAuthSuccess, msgError ->
                Toast.makeText(this, msgError, Toast.LENGTH_SHORT).show()
            }
        }
 

