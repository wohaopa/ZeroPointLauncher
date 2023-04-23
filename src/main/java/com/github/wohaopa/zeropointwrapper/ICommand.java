package com.github.wohaopa.zeropointwrapper;

import java.io.IOException;

public interface ICommand {

    boolean execute(String[] args) throws IOException;

    String usage();
}
