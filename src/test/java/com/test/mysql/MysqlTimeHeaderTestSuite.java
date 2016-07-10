package com.test.mysql;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.jsql.model.InjectionModel;
import com.jsql.model.MediatorModel;
import com.jsql.model.exception.PreparationException;
import com.jsql.model.exception.StoppableException;
import com.jsql.model.strategy.Strategy;
import com.jsql.util.ConnectionUtil;
import com.jsql.view.terminal.SystemOutTerminal;

public class MysqlTimeHeaderTestSuite extends ConcreteMysqlTestSuite {

    @BeforeClass
    public static void initialize() throws PreparationException {
        InjectionModel model = new InjectionModel();
        MediatorModel.register(model);
        model.sendVersionToView();
        new SystemOutTerminal();

        ConnectionUtil.initialUrl = "http://127.0.0.1/simulate_header.php";
        ConnectionUtil.headerData = "lib:1";
        ConnectionUtil.method = "HEADER";

        MediatorModel.model().inputValidation();

        MediatorModel.model().setStrategy(Strategy.TIME);
    }

    @Override
    @Test
    @Ignore
    public void listDatabases() throws PreparationException, StoppableException {
        // Empty on purpose
    }

    @Override
    @Test
    @Ignore
    public void listTables() throws PreparationException, StoppableException {
        // Empty on purpose
    }
}