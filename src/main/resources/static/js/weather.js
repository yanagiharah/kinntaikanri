// 天気の種類に応じたクラスを設定
document.addEventListener('DOMContentLoaded', function() {
	// サーバーからのデータを取得
    const todayWeatherIcon = document.getElementById('todayWeatherIcon');
//	todayWeatherIcon.className = '';
	

    switch (todayWeatherType) {
        case 'Clear'://晴れ
            todayWeatherIcon.classList.add('fa-sun');
            break;
        case 'Clouds'://曇り
            todayWeatherIcon.classList.add('fa-cloud');
            break;
        case 'Rain'://雨
		case 'Drizzle':
            todayWeatherIcon.classList.add('fa-umbrella');
            break;
        case 'Snow'://雪
            todayWeatherIcon.classList.add('fa-snowflake');
            break;
			case 'Mist': // 靄
		    case 'Smoke':// 煙
		    case 'Haze'://霞
		    case 'Dust'://埃
		    case 'Fog'://霧
		    case 'Sand'://砂
		    case 'Ash': // 灰
			todayWeatherIcon.classList.add('fa-smog');
           break;
		case 'Thunderstorm'://雷
			todayWeatherIcon.classList.add('fa-bolt-lightning');
		break;
		case 'Squall'://スコール
			todayWeatherIcon.classList.add('fa-cloud-showers-heavy');
		break;		
		case 'Tornado'://トルネード
			todayWeatherIcon.classList.add('fa-hurricane');
		break;	
        default:
            todayWeatherIcon.classList.add('fa-question'); // 不明な天気の場合
    }
	
	const tomorrowWeatherIcon = document.getElementById('tomorrowWeatherIcon');
	
	switch (tomorrowWeatherType) {
		case 'Clear'://晴れ
		            tomorrowWeatherIcon.classList.add('fa-sun');
		            break;
		        case 'Clouds'://曇り
		            tomorrowWeatherIcon.classList.add('fa-cloud');
		            break;
					case 'Rain'://雨
					case 'Drizzle':
		            tomorrowWeatherIcon.classList.add('fa-umbrella');
		            break;
		        case 'Snow'://雪
		            tomorrowWeatherIcon.classList.add('fa-snowflake');
		            break;
					case 'Mist': // 靄
				    case 'Smoke':// 煙
				    case 'Haze'://霞
				    case 'Dust'://埃
				    case 'Fog'://霧
				    case 'Sand'://砂
				    case 'Ash': // 灰
					tomorrowWeatherIcon.classList.add('fa-smog');
		           break;
				case 'Thunderstorm'://雷
					tomorrowWeatherIcon.classList.add('fa-bolt-lightning');
				break;
				case 'Squall'://スコール
					tomorrowWeatherIcon.classList.add('fa-cloud-showers-heavy');
				break;		
				case 'Tornado'://トルネード
					tomorrowWeatherIcon.classList.add('fa-hurricane');
				break;	
		        default:
		            tomorrowWeatherIcon.classList.add('fa-question'); // 不明な天気の場合
	    }
});