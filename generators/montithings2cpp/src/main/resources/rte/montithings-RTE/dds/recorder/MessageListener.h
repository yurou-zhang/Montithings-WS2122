/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
// (c) https://github.com/MontiCore/monticore
#pragma once
#include <iostream>
#include <functional>
#include <string>
#include <vector>
#include <future>

#include "../../easyloggingpp/easylogging++.h"

#include <dds/DdsDcpsInfrastructureC.h>
#include <dds/DdsDcpsSubscriptionC.h>
#include <dds/DCPS/LocalObject.h>
#include <dds/DCPS/Definitions.h>

#include <dds/DCPS/Marked_Default_Qos.h>
#include <dds/DCPS/StaticIncludes.h>

#include "../message-types/DDSRecorderMessageTypeSupportImpl.h"

#define LOG_ID "RECORDER"

class MessageListener
      : public virtual OpenDDS::DCPS::LocalObject<DDS::DataReaderListener>
{
private:
    const char* RECORDER_MESSAGE_TYPE = "Message Type";
    const char* RECORDER_MESSAGE_TOPIC = "Messages";
    const char* RECORDER_COMMAND_TYPE = "Command Type";
    const char* RECORDER_COMMAND_TOPIC = "Commands";
    const char* RECORDER_COMMANDREPLY_TYPE = "CommandReply Type";
    const char* RECORDER_COMMANDREPLY_TOPIC = "Command Replies";
    const char* RECORDER_ACKNOWLEDGE_TYPE = "Acknowledge Type";
    const char* RECORDER_ACKNOWLEDGE_TOPIC = "Acknowledgements";

    bool isVerbose{false};

    // Allow setting a callback function which is triggered whenever new data arrives
    std::function<void(DDSRecorderMessage::Message)> onRecorderMessageCallback;
    std::function<void(DDSRecorderMessage::Command)> onCommandMessageCallback;
    std::function<void(DDSRecorderMessage::CommandReply)> onCommandReplyMessageCallback;
    std::function<void(DDSRecorderMessage::Acknowledgement)> onAcknowledgementMessageCallback;

    void on_requested_deadline_missed(DDS::DataReader_ptr reader, const DDS::RequestedDeadlineMissedStatus &status) override;
    void on_requested_incompatible_qos(DDS::DataReader_ptr reader, const DDS::RequestedIncompatibleQosStatus &status) override;
    void on_sample_rejected(DDS::DataReader_ptr reader, const DDS::SampleRejectedStatus &status) override;
    void on_liveliness_changed(DDS::DataReader_ptr reader, const DDS::LivelinessChangedStatus &status) override;
    void on_subscription_matched(DDS::DataReader_ptr reader, const DDS::SubscriptionMatchedStatus &status) override;
    void on_sample_lost(DDS::DataReader_ptr reader, const DDS::SampleLostStatus &status) override;
    void on_data_available(DDS::DataReader_ptr reader) override;
    void on_data_available(const DDSRecorderMessage::Message& message);
    void on_data_available(const DDSRecorderMessage::Command& message);
    void on_data_available(const DDSRecorderMessage::CommandReply& message);
    void on_data_available(const DDSRecorderMessage::Acknowledgement& message);

public:
    MessageListener() = default;
    explicit MessageListener(bool verbose) : isVerbose(verbose) {};
    ~MessageListener() = default;

    void addOnRecorderMessageCallback (std::function<void(DDSRecorderMessage::Message)> callback);
    void addOnCommandMessageCallback(std::function<void(DDSRecorderMessage::Command)> callback);
    void addOnCommandReplyMessageCallback(std::function<void(DDSRecorderMessage::CommandReply)> callback);
    void addOnAcknowledgementMessageCallback(std::function<void(DDSRecorderMessage::Acknowledgement)> callback);
};