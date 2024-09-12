# BLE Virtual Bicycle

> BLE(Bluetooth Low Energy)를 사용해서 스마트폰을 이용한 가상 자전거 만들기 toy project
- 스마트폰을 Bluetooth Server로 만들어 센서의 역할을 하게 한다.

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

## Package
```
com.eddy.nrf
├── bluetooth
│   └── ...
├── presentation
│   ├── ui
│   │   ├── component
│   │   │   └── ...
│   │   ├── theme
│   │   │   └── ...
│   │   ├── BikeScreen
│   │   ├── BikeUiState
│   │   └── BikeViewModel
│   └── MainActivcity
└──── utils
```


## Library
| 이름 | 목적| 
|---|---|
| Bluetooth | Bluetooth Low Energy(BLE) 통신을 관리. [BLE 광고](https://developer.android.com/reference/android/bluetooth/le/BluetoothLeAdvertiser) 및 [GATT 서버](https://developer.android.com/reference/android/bluetooth/BluetoothGattServer) 관리에 사용
|Jetpack Compose | 선언적 UI 빌드 및 UI 상태 관리를 위한 라이브러리 |
| Timber | 로그 관리를 위한 라이브러리. 더 간결하고 유연한 로깅을 제공 |

## Architecture
### 안드로이드 권장 앱 아키텍쳐 

- View와 ViewModel의 구조로 관심사 분리를 합니다.
- UI -> **event** -> ViewModel
- ViewModel -> uiState -> UI

<img width="507" alt="image" src="https://github.com/user-attachments/assets/1efb3af7-4310-4f0f-85ac-466fcfc98218">

참고: [ViewModel and State in Compose](https://developer.android.com/codelabs/basic-android-kotlin-compose-viewmodel-and-state?hl=ko#4)
