// (c) https://github.com/MontiCore/monticore
#include "gtest/gtest.h"
#include "Source.h"
#include "Sink.h"
#include "Example.h"
#include "easyloggingpp/easylogging++.h"
#include <chrono>
#include <thread>

INITIALIZE_EASYLOGGINGPP

struct ExampleTest : testing::Test
{
  montithings::hierarchy::Example* cmp;
  montithings::hierarchy::SourceImpl* source;
  montithings::hierarchy::SinkImpl* sink;
  montithings::hierarchy::SourceState sourceState;
  montithings::hierarchy::SinkState sinkState;

  ExampleTest() {
    cmp = new montithings::hierarchy::Example ("example");
    source = new montithings::hierarchy::SourceImpl (sourceState);
    sink = new montithings::hierarchy::SinkImpl (sinkState);
  }
  ~ExampleTest () {
      delete cmp;
      delete source;
      delete sink;
  }
};


TEST_F(ExampleTest, MainTEST) {

    cmp->setUp (TIMESYNC);
    cmp->init ();

    for (int i = 0;i<2;i++)
      {
        auto end = std::chrono::high_resolution_clock::now () + std::chrono::milliseconds (50);
        cmp->compute ();
        do
          {
            std::this_thread::yield ();
            std::this_thread::sleep_for (std::chrono::milliseconds (1));
          }
        while (std::chrono::high_resolution_clock::now () < end);
      }
}