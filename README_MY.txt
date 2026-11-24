KANI OFFLINE MAP - Android starter project

ဒီ ZIP သည် Android Studio ဖြင့် APK တည်ဆောက်ရန် အခြေခံ Project ဖြစ်သည်။

Build လုပ်ရန်
1. Android Studio ကို ကွန်ပျူတာတွင် install လုပ်ပါ။
2. ဒီ ZIP ကို extract လုပ်ပါ။
3. Android Studio > Open > KANI_OFFLINE_MAP_ANDROID folder ကိုရွေးပါ။
4. Gradle Sync ပြီးသည်အထိစောင့်ပါ။
5. Build > Build Bundle(s) / APK(s) > Build APK(s) ကိုနှိပ်ပါ။
6. APK ကို ဖုန်းထဲသို့ကူးပြီး install လုပ်ပါ။

အရေးကြီး
- app/src/main/assets/tiles/ အောက်တွင် tile ဖိုင်များ မပါသေးပါ။
- ထို့ကြောင့် ယခု starter APK တွင် map နောက်ခံအဖြစ် အလွတ်/မီးခိုးရောင်သာ ပေါ်နိုင်သည်။
- Kani မြို့နယ် Satellite Tile များကို သီးခြားရယူပြီး tiles/{zoom}/{x}/{y}.jpg ပုံစံဖြင့် ထည့်ရမည်။
- Satellite imagery provider ၏ offline download၊ caching၊ redistribution လိုင်စင်ကို လိုက်နာရမည်။
- GPS tracking သည် App ဖွင့်ထားစဉ်သာ အလုပ်လုပ်မည်။ Background tracking အတွက် နောက်ထပ် foreground service နှင့် notification လိုအပ်သည်။
- Location permission ကို ဖုန်းက မေးလာလျှင် Allow လုပ်ပါ။

\nCloud APK Build (Android Studio မလိုဘဲ)
1. GitHub account တစ်ခုဖွင့်ပါ။
2. GitHub တွင် repository အသစ်တစ်ခုဖန်တီးပါ။
3. ZIP ကို extract လုပ်ပြီး အတွင်းရှိ project ဖိုင်အားလုံးကို repository သို့ upload လုပ်ပါ။
4. GitHub repository > Actions > Build KANI OFFLINE MAP APK > Run workflow ကိုနှိပ်ပါ။
5. Build အောင်မြင်လျှင် workflow run အောက်ရှိ Artifacts မှ kani-offline-map-debug-apk ကို download လုပ်ပါ။
6. ZIP ဖြည်ပြီး app-debug.apk ကို Android ဖုန်းတွင် install လုပ်ပါ။

မှတ်ချက်: GitHub workflow က APK ကို build ပေးနိုင်သော်လည်း Satellite Tile Data ကို အလိုအလျောက်မဖန်တီးပါ။
