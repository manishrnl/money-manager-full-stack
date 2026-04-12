```bash

    git init
    git add .
    git commit -m "Part 1"
    git branch -M main
    git remote add origin https://github.com/manishrnl/money-manager-backend.git
    git push -u origin main
    
```

################# Kiil Port
#To kill task like port 8080 , tun following command in cmd as an administration

```bash

  for /f "tokens=5" %a in ('netstat -aon ^| findstr :8080 ^| findstr LISTENING') do taskkill /PID %a /F


```


