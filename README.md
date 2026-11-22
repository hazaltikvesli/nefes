# Nefes

Sigara farkındalık ve şınav cezası uygulaması (Android).

## APK'yı GitHub üzerinde derlemek (önerilen)
1. Bu klasörü GitHub'da yeni bir depoya yükle (`main` dalı).
2. Depoda **Actions** sekmesine gir; "APK oluştur" iş akışı kendiliğinden çalışır.
3. Bitince (yeşil tik) çalıştırmaya tıkla, en alttaki **Artifacts** bölümünden `nefes-apk` dosyasını indir.
4. Zip'i aç; içindeki `app-debug.apk` dosyasını telefona gönder ve kur.

## Bilgisayarda derlemek
Gerekenler: Node.js 22+, Android Studio (JDK 21 ile gelir).

```bash
npm install
npx cap sync android
npx cap open android      # Android Studio açılır: Build > Build APK(s)
```

APK yolu: `android/app/build/outputs/apk/debug/app-debug.apk`

## Uygulamayı değiştirmek
Tüm arayüz ve mantık `www/index.html` içinde. Değiştirdikten sonra `npx cap sync android` çalıştır
(GitHub'a yüklediysen sadece push etmen yeterli; Actions yeni APK üretir).
Yeni sürümde `android/app/build.gradle` içindeki `versionCode` değerini 1 artır.

## Veriler
Kayıtlar telefonda, uygulamanın kendi depolama alanında tutulur. Kamera görüntüsü kaydedilmez ve
hiçbir yere gönderilmez; hareket takibi tamamen telefonda çalışır. Uygulama silinirse veriler de silinir.
