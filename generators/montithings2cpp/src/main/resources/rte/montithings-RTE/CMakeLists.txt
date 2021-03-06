cmake_minimum_required(VERSION 3.8.2)
project("MontiThingsRTE")
set(CMAKE_CXX_STANDARD 11)

# Find all subdirectories with .h files
# Adapted from https://stackoverflow.com/a/31004567
MACRO(HEADER_DIRECTORIES input return_list)
    FILE(GLOB_RECURSE new_list ${input}/*.h)
    SET(dir_list "")
    FOREACH (file_path ${new_list})
        GET_FILENAME_COMPONENT(dir_path ${file_path} PATH)
        SET(dir_list ${dir_list} ${dir_path})
    ENDFOREACH ()
    LIST(REMOVE_DUPLICATES dir_list)
    SET(${return_list} ${dir_list})
ENDMACRO()
SET(dir_list "")

if (NOT EXCLUDE_DDS)
    find_package(OpenDDS REQUIRED)

    set(CMAKE_CXX_COMPILER "${OPENDDS_COMPILER}")
    set(opendds_libs
        OpenDDS::Dcps # Core OpenDDS Library
        OpenDDS::InfoRepoDiscovery
        OpenDDS::Tcp
        OpenDDS::Rtps
        OpenDDS::Udp
        OpenDDS::Rtps_Udp
        OpenDDS::Multicast
        OpenDDS::Shmem
    )
endif ()

find_package(nng 1.1.1 CONFIG REQUIRED)

# for MSVC
if (MSVC)
    set(variables
            CMAKE_CXX_FLAGS_DEBUG
            CMAKE_CXX_FLAGS_RELEASE
            CMAKE_CXX_FLAGS_RELWITHDEBINFO
            CMAKE_CXX_FLAGS_MINSIZEREL
            )
    foreach (variable ${variables})
        if (${variable} MATCHES "/MD")
            string(REGEX REPLACE "/MD" "/MT" ${variable} "${${variable}}")
        endif ()
    endforeach ()
endif ()

#set target for building executables and libraries
set(CMAKE_ARCHIVE_OUTPUT_DIRECTORY ${CMAKE_BINARY_DIR}/lib)
set(CMAKE_LIBRARY_OUTPUT_DIRECTORY ${CMAKE_BINARY_DIR}/lib)
set(CMAKE_RUNTIME_OUTPUT_DIRECTORY ${CMAKE_BINARY_DIR}/bin)

file(GLOB_RECURSE RTE_SOURCES "./*.cpp" "./*.h")
include_directories(".")

# swap out log() method from MTLibrary if log tracing is enabled
list(FILTER RTE_SOURCES EXCLUDE REGEX "Log.cpp")
if (ENABLE_LOG_TRACING)
    list(APPEND RTE_SOURCES "${CMAKE_CURRENT_SOURCE_DIR}/logtracing/mtlibrary-overwrites/Log.cpp")
else ()
    list(APPEND RTE_SOURCES "${CMAKE_CURRENT_SOURCE_DIR}/mtlibrary/Log.cpp")

    # remove all related files
    list(FILTER RTE_SOURCES EXCLUDE REGEX "logtracing/*")
    list(FILTER RTE_SOURCES EXCLUDE REGEX "logtracing/data/*")
    list(FILTER RTE_SOURCES EXCLUDE REGEX "logtracing/interface/dds/*")
    list(FILTER RTE_SOURCES EXCLUDE REGEX "logtracing/interface/dds/message-types/*")
    list(FILTER RTE_SOURCES EXCLUDE REGEX "logtracing/interface/mqtt/*")
    list(FILTER RTE_SOURCES EXCLUDE REGEX "logtracing/interface/mqtt/message-types/*")
endif ()

# Exclude DDS files
# Note that we still need record-and-replay/*/*, in case DDS is not excluded, as the recorder module is referenced in DDSPort.h
if (EXCLUDE_DDS)
    list(FILTER RTE_SOURCES EXCLUDE REGEX "DDS.*.h")
    list(FILTER RTE_SOURCES EXCLUDE REGEX "DDS.*.cpp")
    list(FILTER RTE_SOURCES EXCLUDE REGEX "dds/message-types/*")
    list(FILTER RTE_SOURCES EXCLUDE REGEX "record-and-replay/message-types/*")
    list(FILTER RTE_SOURCES EXCLUDE REGEX "record-and-replay/recorder/*")
    list(FILTER RTE_SOURCES EXCLUDE REGEX "logtracing/interface/dds/*")
    list(FILTER RTE_SOURCES EXCLUDE REGEX "logtracing/interface/dds/message-types/*")
endif ()

# Keep hwc interceptor. 
# This makes sure that the nd() method is still present even if record and replay is not enabled.
set(RTE_SOURCES
    ${RTE_SOURCES}
    "record-and-replay/recorder/HWCInterceptor.h"
    "record-and-replay/recorder/HWCInterceptor.cpp"
) 


# exclude MQTT related part of the RTE to not require Mosquitto for compiling
if (EXCLUDE_MQTT)
    list(FILTER RTE_SOURCES EXCLUDE REGEX "Mqtt.*.h")
    list(FILTER RTE_SOURCES EXCLUDE REGEX "Mqtt.*.cpp")
    list(FILTER RTE_SOURCES EXCLUDE REGEX "logtracing/interface/mqtt/*")
    list(FILTER RTE_SOURCES EXCLUDE REGEX "logtracing/interface/mqtt/message-types/*")
endif ()

if (EXCLUDE_COMM_MANAGER)
    # Exclude management communication because splitting is disabled
    list(FILTER RTE_SOURCES EXCLUDE REGEX "ManagementCommunication.h")
    list(FILTER RTE_SOURCES EXCLUDE REGEX "ManagementCommunication.cpp")
endif ()

add_library(MontiThingsRTE ${RTE_SOURCES})

if (NOT EXCLUDE_MQTT)
    # Include Mosquitto Library
    find_library(MOSQUITTO_LIB mosquitto HINTS /usr/local/Cellar/mosquitto)
    target_link_libraries(MontiThingsRTE ${MOSQUITTO_LIB})
endif ()

if (NOT EXCLUDE_DDS)
    target_link_libraries(MontiThingsRTE ${opendds_libs})
endif ()

target_link_libraries(MontiThingsRTE nng::nng)
set_target_properties(MontiThingsRTE PROPERTIES LINKER_LANGUAGE CXX)
install(TARGETS MontiThingsRTE DESTINATION ${PROJECT_SOURCE_DIR}/lib)
