package com.xpfriend.tydrone;

import com.xpfriend.tydrone.core.Facade;
import com.xpfriend.tydrone.core.Info;
import com.xpfriend.tydrone.core.Runner;
import com.xpfriend.tydrone.factory.SimpleStartableFactory;

import java.io.IOException;

public class SimpleMain extends Facade {
    @Override
    protected void handleRun(Info info) throws IOException {
        SimpleStartableFactory factory = SimpleStartableFactory.getInstance();
        new Runner(factory.getLogger()).run(info, factory.createStartables());
    }
}
