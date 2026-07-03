package com.example.util

object LogoResolver {
    fun getLogoUrl(channelName: String): String? {
        val name = channelName.lowercase().trim()
        return when {
            // ESPN Channels
            name.contains("espn") -> {
                when {
                    name.contains("premium") -> "https://upload.wikimedia.org/wikipedia/commons/thumb/c/c2/ESPN_Premium_logo.svg/512px-ESPN_Premium_logo.svg.png"
                    name.contains("deport") -> "https://upload.wikimedia.org/wikipedia/commons/thumb/f/f6/ESPN_Deportes_logo.svg/512px-ESPN_Deportes_logo.svg.png"
                    else -> "https://upload.wikimedia.org/wikipedia/commons/thumb/2/2f/ESPN_wordmark.svg/512px-ESPN_wordmark.svg.png"
                }
            }
            // TNT Sports Channels
            name.contains("tnt sport") -> {
                "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d4/TNT_Sports_logo_2023.svg/512px-TNT_Sports_logo_2023.svg.png"
            }
            // TyC Sports Channels
            name.contains("tyc") -> {
                "https://upload.wikimedia.org/wikipedia/commons/thumb/c/ca/TyC_Sports_logo_2017.svg/512px-TyC_Sports_logo_2017.svg.png"
            }
            // Fox Sports Channels
            name.contains("fox sport") || name.contains("fox deports") -> {
                "https://upload.wikimedia.org/wikipedia/commons/thumb/3/36/Fox_Sports_logo.svg/512px-Fox_Sports_logo.svg.png"
            }
            name == "fox" || name == "fox 2" || name.contains("fox hd") -> {
                "https://upload.wikimedia.org/wikipedia/commons/thumb/6/62/Fox_Corporation_logo.svg/512px-Fox_Corporation_logo.svg.png"
            }
            // Win Sports Channels
            name.contains("win sport") || name == "win" || name == "win+" || name == "win2" || name.contains("win sport") -> {
                "https://upload.wikimedia.org/wikipedia/commons/3/33/Win_Sports_Logo.png"
            }
            // DirecTV Sports / DSports
            name.contains("dsports") || name.contains("directv sport") || name.contains("dsport") -> {
                "https://upload.wikimedia.org/wikipedia/commons/thumb/7/7b/DSports_logo_2022.svg/512px-DSports_logo_2022.svg.png"
            }
            // TUDN
            name.contains("tudn") -> {
                "https://upload.wikimedia.org/wikipedia/commons/thumb/7/76/TUDN_logo.svg/512px-TUDN_logo.svg.png"
            }
            // Claro Sports
            name.contains("claro sport") -> {
                "https://upload.wikimedia.org/wikipedia/commons/thumb/f/fa/Logo_Claro_Sports.svg/512px-Logo_Claro_Sports.svg.png"
            }
            // Eurosport
            name.contains("eurosport") -> {
                "https://upload.wikimedia.org/wikipedia/commons/thumb/2/23/Eurosport_logo_2015.svg/512px-Eurosport_logo_2015.svg.png"
            }
            // T Sports
            name.contains("t sports") || name.contains("tsports") -> {
                "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d4/TNT_Sports_logo_2023.svg/512px-TNT_Sports_logo_2023.svg.png"
            }
            // beIN Sports
            name.contains("bein") -> {
                "https://upload.wikimedia.org/wikipedia/commons/thumb/1/1a/BeIN_Sports_logo.svg/512px-BeIN_Sports_logo.svg.png"
            }
            // FIFA Sports
            name.contains("fifa") -> {
                "https://upload.wikimedia.org/wikipedia/commons/thumb/a/aa/FIFA_logo_without_slogan.svg/512px-FIFA_logo_without_slogan.svg.png"
            }
            // DAZN
            name.contains("dazn") || name.contains("eleven") -> {
                "https://upload.wikimedia.org/wikipedia/commons/thumb/1/11/DAZN_logo.svg/512px-DAZN_logo.svg.png"
            }
            // Match TV (Матч!)
            name.contains("match") || name.contains("матч") -> {
                "https://upload.wikimedia.org/wikipedia/commons/thumb/9/9c/Match_TV_logo.svg/512px-Match_TV_logo.svg.png"
            }
            // Telefe
            name.contains("telefe") -> {
                "https://upload.wikimedia.org/wikipedia/commons/thumb/9/90/Logo_de_Telefe.svg/512px-Logo_de_Telefe.svg.png"
            }
            // Real Madrid TV
            name.contains("real madrid") -> {
                "https://upload.wikimedia.org/wikipedia/commons/thumb/3/3f/Real_Madrid_TV_logo.svg/512px-Real_Madrid_TV_logo.svg.png"
            }
            // Telemundo
            name.contains("telemundo") -> {
                "https://upload.wikimedia.org/wikipedia/commons/thumb/4/47/Telemundo_2018_Logo_Standard_RGB.svg/512px-Telemundo_2018_Logo_Standard_RGB.svg.png"
            }
            // Red Bull TV
            name.contains("red bull") -> {
                "https://upload.wikimedia.org/wikipedia/commons/thumb/9/9a/Red_Bull_TV_logo.svg/512px-Red_Bull_TV_logo.svg.png"
            }
            // ČT Sport
            name.contains("ct sport") || name.contains("čt sport") -> {
                "https://upload.wikimedia.org/wikipedia/commons/thumb/2/29/%C4%8CT_Sport_logo_2012.svg/512px-%C4%8CT_Sport_logo_2012.svg.png"
            }
            // ORF
            name.contains("orf") -> {
                "https://upload.wikimedia.org/wikipedia/commons/thumb/3/30/ORF2_logo%2C_white_on_red_background.png/512px-ORF2_logo%2C_white_on_red_background.png"
            }
            // SuperSport
            name.contains("super sport") || name.contains("supersport") -> {
                "https://upload.wikimedia.org/wikipedia/commons/thumb/f/fb/SuperSport_logo_2020.svg/512px-SuperSport_logo_2020.svg.png"
            }
            // Max Sport
            name.contains("max sport") -> {
                "https://upload.wikimedia.org/wikipedia/commons/thumb/1/18/Max_Sport_logo_horizontal.png/512px-Max_Sport_logo_horizontal.png"
            }
            // Ziggo Sport
            name.contains("ziggo") -> {
                "https://upload.wikimedia.org/wikipedia/commons/thumb/a/a9/Ziggo_Sport_logo_2017.svg/512px-Ziggo_Sport_logo_2017.svg.png"
            }
            // Gol TV
            name.contains("gol tv") -> {
                "https://upload.wikimedia.org/wikipedia/commons/thumb/7/7a/Gol_TV_logo.png/512px-Gol_TV_logo.png"
            }
            // TV Azteca / Azteca Uno / Azteca 7
            name.contains("azteca") -> {
                "https://upload.wikimedia.org/wikipedia/commons/thumb/6/6f/TV_Azteca_logo_2015.svg/512px-TV_Azteca_logo_2015.svg.png"
            }
            // Tigo Sports
            name.contains("tigo") -> {
                "https://upload.wikimedia.org/wikipedia/commons/thumb/2/20/Logo_Tigo_Sports.svg/512px-Logo_Tigo_Sports.svg.png"
            }
            // NBC Universo
            name.contains("universo") -> {
                "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d4/Universo_logo_2017.svg/512px-Universo_logo_2017.svg.png"
            }
            // NBC / NBC Team USA
            name.contains("nbc") -> {
                "https://upload.wikimedia.org/wikipedia/commons/thumb/3/3f/NBC_logo.svg/512px-NBC_logo.svg.png"
            }
            // Sky Sport
            name.contains("sky sport") -> {
                "https://upload.wikimedia.org/wikipedia/commons/thumb/3/3e/Sky_Sports_logo_2020.svg/512px-Sky_Sports_logo_2020.svg.png"
            }
            // ETC TV
            name.contains("etc tv") || name == "etc" -> {
                "https://upload.wikimedia.org/wikipedia/commons/thumb/f/f3/Logo_ETC.svg/512px-Logo_ETC.svg.png"
            }
            // Adrenalina Sports
            name.contains("adrenalina") -> {
                "https://upload.wikimedia.org/wikipedia/commons/c/c5/Adrenalina_Logo_2019.png"
            }
            // L'Equipe
            name.contains("equipe") -> {
                "https://upload.wikimedia.org/wikipedia/commons/thumb/3/3b/L%27%C3%89quipe_logo_2015.svg/512px-L%27%C3%89quipe_logo_2015.svg.png"
            }
            // Canal+
            name.contains("canal sport") || name.contains("canal+") -> {
                "https://upload.wikimedia.org/wikipedia/commons/thumb/0/00/Canal%2B_Logo.svg/512px-Canal%2B_Logo.svg.png"
            }
            // Setanta Sports
            name.contains("setanta") || name.contains("setenta") -> {
                "https://upload.wikimedia.org/wikipedia/commons/thumb/a/ad/Setanta_Sports_Logo.svg/512px-Setanta_Sports_Logo.svg.png"
            }
            // Digi Sport
            name.contains("digi sport") -> {
                "https://upload.wikimedia.org/wikipedia/commons/thumb/b/b5/Digi_Sport_logo_2021.png/512px-Digi_Sport_logo_2021.png"
            }
            // Idman TV
            name.contains("idman") -> {
                "https://upload.wikimedia.org/wikipedia/commons/3/38/%C4%B0dman_Az%C9%99rbaycan_TV_logo.jpg"
            }
            // Suspilne Sport
            name.contains("suspi") || name.contains("суспільне") -> {
                "https://upload.wikimedia.org/wikipedia/commons/thumb/8/87/Suspilne_Sport_Logo.svg/512px-Suspilne_Sport_Logo.svg.png"
            }
            // CazéTV
            name.contains("caze") -> {
                "https://upload.wikimedia.org/wikipedia/commons/thumb/5/52/Caz%C3%A9TV_Logo.png/512px-Caz%C3%A9TV_Logo.png"
            }
            // El Garage TV
            name.contains("garage") -> {
                "https://upload.wikimedia.org/wikipedia/commons/thumb/4/4b/El_garage_tv_logo.png/512px-El_garage_tv_logo.png"
            }
            else -> null
        }
    }
}
