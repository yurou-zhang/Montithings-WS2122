/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
// (c) https://github.com/MontiCore/monticore
#pragma once
#include <fstream>
#include <string>
#include <thread>
#include <vector>

#include "../montithings-RTE/json/json.hpp"
#include "lib/loguru.hpp"

#include "../montithings-RTE/dds/recorder/DDSCommunicator.h"
#include "../montithings-RTE/dds/recorder/utils.h"
#include "RecordProcessor.h"

class MessageFlowRecorder
{
private:
  // maintains DDS entities such as the participant, subscriber, data readers, ...
  DDSCommunicator ddsCommunicator;

  // determines current mode
  bool isRecording{};

  // amount of instances the recorder should wait for to connect until the actual recording is
  // started
  int instanceAmount = 1;

  // output file path for recordings
  std::string fileRecordingsPath;

  // stores all incoming messages unprocessed
  std::vector<DDSRecorderMessage::Message> recordedMessages;

  // storages for processed messages, non-deterministic calls, and computation latencies
  nlohmann::json recordStorage;

  // simple counters which keep track of how many non-deterministic calls and computation latencies
  // have been received; only for logging purposes
  int statsCallsAmount = 0;
  int statsLatenciesAmount = 0;

  // event handlers for all types of DDS messages
  void onDebugMessage (const DDSRecorderMessage::Message &message);
  void onCommandReplyMessage (const DDSRecorderMessage::CommandReply &message);
  void onAcknowledgementMessage (const DDSRecorderMessage::Acknowledgement &message);

  void logProgress ();

public:
  MessageFlowRecorder () = default;
  ~MessageFlowRecorder () = default;

  // setter
  void setFileRecordings (std::string &filePath);
  void setDcpsInfoRepoHost (std::string &host);
  void setVerbose (bool verbose);
  void setInstanceAmount (int n);

  void init ();
  void start ();
  void stop ();
  void cleanup ();
  void process ();
  void saveToFile ();
};