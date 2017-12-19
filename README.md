## SIMDA _ "심장을 다스리다"

------

### 작성자

- 충남대학교 손민희
- 충남대학교 유단비
- 충남대학교 한아름



### 개발 환경

- Android Studio 
  - Target SDK : 23
- Arduino UNO 
  - PPG Sensor
  - Bluetooth (HC-06)

------

## IDEATION

<img src="https://github.com/minheeson/SIMDA/blob/master/screenshots/car.png" width=400/>

##### 1) 운전 상황과 골든타임

- 운전 중 졸음이 오거나 심장정지, 발작 등의 문제가 발생한다면, 큰 사고를 초래할 수 있음


- 위급상황 발생시, 골든타임(4분) 내 심폐소생술 또는 자동제세동기를 통한 구조가 필요함

##### 2) eCall 서비스

- Emergency Call의 약자로, 비상 호출 시스템을 의미함
- 자동차에서 에어백 등이 작동하는 사고가 발생하면, 이를 자동으로 인지해 신고와 구호를 요청하고 차량 내에 설치된 비상 버튼을 누르면 EU 긴급 번호인 112로 연결됨 

##### 3) PPG 센서

- 운전자의 실시간 상태 판별을 위해 PPG 센서를 통해 심박수와 혈류량 데이터


## DETAILS

#### 1. 회로 구성

<img src="https://github.com/minheeson/SIMDA/blob/master/screenshots/CircuitConfiguration.png" width="400"/>

#### 2. 화면 구성 

|                  NORMAL                  |                  SLEEPY                  |                 EXCITED                  |                  FATAL                   |              eCall Service               |
| :--------------------------------------: | :--------------------------------------: | :--------------------------------------: | :--------------------------------------: | :--------------------------------------: |
| <img src="https://github.com/minheeson/SIMDA/blob/master/screenshots/SIMDA_status_normal.png" width=150/> | <img src="https://github.com/minheeson/SIMDA/blob/master/screenshots/SIMDA_status_sleepy.png" width=150/> | <img src="https://github.com/minheeson/SIMDA/blob/master/screenshots/SIMDA_status_excited.png" width=150/> | <img src="https://github.com/minheeson/SIMDA/blob/master/screenshots/SIMDA_status_fatal.png" width=150/> | <img src="https://github.com/minheeson/SIMDA/blob/master/screenshots/SIMDA_ecall.png" width=150/> |

##### 1) NORMAL STATE

- 정상 수치 : 70~120 mmHg
- 심박수 모니터링 

##### 2) SLEEPY/EXCITED STATE

- 음악 서비스 제공 

##### 3) FATAL STATE

- 위급 수치 : 70 mmHg 미만, 120 mmHg 초과
- eCall 서비스 자동 호출



## PANEL

<img src="https://github.com/minheeson/SIMDA/blob/master/screenshots/simdaPanel.png" width="800"/>

