# BLE Virtual Bicycle

> BLE(Bluetooth Low Energy)를 사용해서 스마트폰을 이용한 가상 자전거 만들기 toy project

- 스마트폰을 Bluetooth Server로 만들어 센서의 역할을 하게 한다.

## using
- [BluetoothLeAdvertiser](https://developer.android.com/reference/android/bluetooth/le/BluetoothLeAdvertiser)
    - [AdvertiseCallback](https://developer.android.com/reference/android/bluetooth/le/AdvertiseCallback)    
- [BluetoothGattServer](https://developer.android.com/reference/android/bluetooth/BluetoothGattServer)
    - [BluetoothGattServerCallback](https://developer.android.com/reference/android/bluetooth/BluetoothGattServerCallback)
- Animation


## 기기

안드로이드 스마트폰 기기 2개가 필요하다.
앱을 설치한 기기가 센서의 역할을 하며 정보를 생성하고 전달하는 `server`가 된다.
앱을 설치하지 않은 기기는 `Server`로 부터 정보를 받아오는 `client` 기기가 된다.

## 시나리오

1. 블루투스 기기 광고를 한다.
2. 블루투스 연결을 당한다.
3. app이 설치된 기기(`server`)에서 연결당한 `client` 기기에게 정보를 전송한다.
    - `BluetoothGattServerCallback` 안에 있는 `onConnectionStateChange`를 통해 연결 감지, 연결된 `client` 기기 정보(Address)를 불러온다.
    - `onCharacteristicReadRequest`를 통해 연결된 `client` 기기에게 정보를 전송한다.
## trouble shooting
```
error 133 (0x85) gatt error
```
- 크기가 큼


## 깨알 상식
- 블루투스는 1:1 연결이다.
- 가상 기기는 블루투스가 없다. 
