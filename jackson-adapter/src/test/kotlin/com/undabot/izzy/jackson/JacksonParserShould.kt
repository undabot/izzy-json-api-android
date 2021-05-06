package com.undabot.izzy.jackson

import com.undabot.izzy.parser.IzzyConfiguration
import com.undabot.izzy.parser.IzzyJsonParserShould

class JacksonParserShould : IzzyJsonParserShould(JacksonParser(IzzyConfiguration()))
