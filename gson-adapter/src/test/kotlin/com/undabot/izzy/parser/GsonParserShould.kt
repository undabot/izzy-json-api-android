package com.undabot.izzy.parser

import com.google.gson.Gson

class GsonParserShould : IzzyJsonParserShould(GsonParser(IzzyConfiguration(), Gson()))
