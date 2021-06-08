// -*- C++ -*-
// $Id$

/**
 * Code generated by the The ACE ORB (TAO) IDL Compiler v2.2a_p19
 * TAO and the TAO IDL Compiler have been developed by:
 *       Center for Distributed Object Computing
 *       Washington University
 *       St. Louis, MO
 *       USA
 *       http://www.cs.wustl.edu/~schmidt/doc-center.html
 * and
 *       Distributed Object Computing Laboratory
 *       University of California at Irvine
 *       Irvine, CA
 *       USA
 * and
 *       Institute for Software Integrated Systems
 *       Vanderbilt University
 *       Nashville, TN
 *       USA
 *       http://www.isis.vanderbilt.edu/
 *
 * Information about TAO is available at:
 *     http://www.cs.wustl.edu/~schmidt/TAO.html
 **/

// TAO_IDL - Generated from
// be/be_codegen.cpp:376


#include "DDSMessageTypeSupportC.h"
#include "tao/CDR.h"
#include "ace/OS_NS_string.h"

#if !defined (__ACE_INLINE__)
#include "DDSMessageTypeSupportC.inl"
#endif /* !defined INLINE */

// TAO_IDL - Generated from
// be/be_visitor_interface/interface_cs.cpp:51

// Traits specializations for DDSMessage::MessageTypeSupport.

DDSMessage::MessageTypeSupport_ptr
TAO::Objref_Traits<DDSMessage::MessageTypeSupport>::duplicate (
    DDSMessage::MessageTypeSupport_ptr p)
{
  return DDSMessage::MessageTypeSupport::_duplicate (p);
}

void
TAO::Objref_Traits<DDSMessage::MessageTypeSupport>::release (
    DDSMessage::MessageTypeSupport_ptr p)
{
  ::CORBA::release (p);
}

DDSMessage::MessageTypeSupport_ptr
TAO::Objref_Traits<DDSMessage::MessageTypeSupport>::nil (void)
{
  return DDSMessage::MessageTypeSupport::_nil ();
}

::CORBA::Boolean
TAO::Objref_Traits<DDSMessage::MessageTypeSupport>::marshal (
    const DDSMessage::MessageTypeSupport_ptr p,
    TAO_OutputCDR & cdr)
{
  return ::CORBA::Object::marshal (p, cdr);
}

DDSMessage::MessageTypeSupport::MessageTypeSupport (void)
{}

DDSMessage::MessageTypeSupport::~MessageTypeSupport (void)
{
}

DDSMessage::MessageTypeSupport_ptr
DDSMessage::MessageTypeSupport::_narrow (
    ::CORBA::Object_ptr _tao_objref)
{
  return MessageTypeSupport::_duplicate (
      dynamic_cast<MessageTypeSupport_ptr> (_tao_objref)
    );
}

DDSMessage::MessageTypeSupport_ptr
DDSMessage::MessageTypeSupport::_unchecked_narrow (
    ::CORBA::Object_ptr _tao_objref)
{
  return MessageTypeSupport::_duplicate (
      dynamic_cast<MessageTypeSupport_ptr> (_tao_objref)
    );
}

DDSMessage::MessageTypeSupport_ptr
DDSMessage::MessageTypeSupport::_nil (void)
{
  return 0;
}

DDSMessage::MessageTypeSupport_ptr
DDSMessage::MessageTypeSupport::_duplicate (MessageTypeSupport_ptr obj)
{
  if (! ::CORBA::is_nil (obj))
    {
      obj->_add_ref ();
    }
  return obj;
}

void
DDSMessage::MessageTypeSupport::_tao_release (MessageTypeSupport_ptr obj)
{
  ::CORBA::release (obj);
}

::CORBA::Boolean
DDSMessage::MessageTypeSupport::_is_a (const char *value)
{
  if (
      ACE_OS::strcmp (
          value,
          "IDL:DDS/TypeSupport:1.0"
        ) == 0 ||
      ACE_OS::strcmp (
          value,
          "IDL:OpenDDS/DCPS/TypeSupport:1.0"
        ) == 0 ||
      ACE_OS::strcmp (
          value,
          "IDL:DDSMessage/MessageTypeSupport:1.0"
        ) == 0 ||
      ACE_OS::strcmp (
          value,
          "IDL:omg.org/CORBA/LocalObject:1.0"
        ) == 0 ||
      ACE_OS::strcmp (
          value,
          "IDL:omg.org/CORBA/Object:1.0"
        ) == 0
    )
    {
      return true; // success using local knowledge
    }
  else
    {
      return false;
    }
}

const char* DDSMessage::MessageTypeSupport::_interface_repository_id (void) const
{
  return "IDL:DDSMessage/MessageTypeSupport:1.0";
}

::CORBA::Boolean
DDSMessage::MessageTypeSupport::marshal (TAO_OutputCDR & /* cdr */)
{
  return false;
}

// TAO_IDL - Generated from
// be/be_visitor_interface/interface_cs.cpp:51

// Traits specializations for DDSMessage::MessageDataWriter.

DDSMessage::MessageDataWriter_ptr
TAO::Objref_Traits<DDSMessage::MessageDataWriter>::duplicate (
    DDSMessage::MessageDataWriter_ptr p)
{
  return DDSMessage::MessageDataWriter::_duplicate (p);
}

void
TAO::Objref_Traits<DDSMessage::MessageDataWriter>::release (
    DDSMessage::MessageDataWriter_ptr p)
{
  ::CORBA::release (p);
}

DDSMessage::MessageDataWriter_ptr
TAO::Objref_Traits<DDSMessage::MessageDataWriter>::nil (void)
{
  return DDSMessage::MessageDataWriter::_nil ();
}

::CORBA::Boolean
TAO::Objref_Traits<DDSMessage::MessageDataWriter>::marshal (
    const DDSMessage::MessageDataWriter_ptr p,
    TAO_OutputCDR & cdr)
{
  return ::CORBA::Object::marshal (p, cdr);
}

DDSMessage::MessageDataWriter::MessageDataWriter (void)
{}

DDSMessage::MessageDataWriter::~MessageDataWriter (void)
{
}

DDSMessage::MessageDataWriter_ptr
DDSMessage::MessageDataWriter::_narrow (
    ::CORBA::Object_ptr _tao_objref)
{
  return MessageDataWriter::_duplicate (
      dynamic_cast<MessageDataWriter_ptr> (_tao_objref)
    );
}

DDSMessage::MessageDataWriter_ptr
DDSMessage::MessageDataWriter::_unchecked_narrow (
    ::CORBA::Object_ptr _tao_objref)
{
  return MessageDataWriter::_duplicate (
      dynamic_cast<MessageDataWriter_ptr> (_tao_objref)
    );
}

DDSMessage::MessageDataWriter_ptr
DDSMessage::MessageDataWriter::_nil (void)
{
  return 0;
}

DDSMessage::MessageDataWriter_ptr
DDSMessage::MessageDataWriter::_duplicate (MessageDataWriter_ptr obj)
{
  if (! ::CORBA::is_nil (obj))
    {
      obj->_add_ref ();
    }
  return obj;
}

void
DDSMessage::MessageDataWriter::_tao_release (MessageDataWriter_ptr obj)
{
  ::CORBA::release (obj);
}

::CORBA::Boolean
DDSMessage::MessageDataWriter::_is_a (const char *value)
{
  if (
      ACE_OS::strcmp (
          value,
          "IDL:DDS/Entity:1.0"
        ) == 0 ||
      ACE_OS::strcmp (
          value,
          "IDL:DDS/DataWriter:1.0"
        ) == 0 ||
      ACE_OS::strcmp (
          value,
          "IDL:DDSMessage/MessageDataWriter:1.0"
        ) == 0 ||
      ACE_OS::strcmp (
          value,
          "IDL:omg.org/CORBA/LocalObject:1.0"
        ) == 0 ||
      ACE_OS::strcmp (
          value,
          "IDL:omg.org/CORBA/Object:1.0"
        ) == 0
    )
    {
      return true; // success using local knowledge
    }
  else
    {
      return false;
    }
}

const char* DDSMessage::MessageDataWriter::_interface_repository_id (void) const
{
  return "IDL:DDSMessage/MessageDataWriter:1.0";
}

::CORBA::Boolean
DDSMessage::MessageDataWriter::marshal (TAO_OutputCDR & /* cdr */)
{
  return false;
}

// TAO_IDL - Generated from
// be/be_visitor_interface/interface_cs.cpp:51

// Traits specializations for DDSMessage::MessageDataReader.

DDSMessage::MessageDataReader_ptr
TAO::Objref_Traits<DDSMessage::MessageDataReader>::duplicate (
    DDSMessage::MessageDataReader_ptr p)
{
  return DDSMessage::MessageDataReader::_duplicate (p);
}

void
TAO::Objref_Traits<DDSMessage::MessageDataReader>::release (
    DDSMessage::MessageDataReader_ptr p)
{
  ::CORBA::release (p);
}

DDSMessage::MessageDataReader_ptr
TAO::Objref_Traits<DDSMessage::MessageDataReader>::nil (void)
{
  return DDSMessage::MessageDataReader::_nil ();
}

::CORBA::Boolean
TAO::Objref_Traits<DDSMessage::MessageDataReader>::marshal (
    const DDSMessage::MessageDataReader_ptr p,
    TAO_OutputCDR & cdr)
{
  return ::CORBA::Object::marshal (p, cdr);
}

DDSMessage::MessageDataReader::MessageDataReader (void)
{}

DDSMessage::MessageDataReader::~MessageDataReader (void)
{
}

DDSMessage::MessageDataReader_ptr
DDSMessage::MessageDataReader::_narrow (
    ::CORBA::Object_ptr _tao_objref)
{
  return MessageDataReader::_duplicate (
      dynamic_cast<MessageDataReader_ptr> (_tao_objref)
    );
}

DDSMessage::MessageDataReader_ptr
DDSMessage::MessageDataReader::_unchecked_narrow (
    ::CORBA::Object_ptr _tao_objref)
{
  return MessageDataReader::_duplicate (
      dynamic_cast<MessageDataReader_ptr> (_tao_objref)
    );
}

DDSMessage::MessageDataReader_ptr
DDSMessage::MessageDataReader::_nil (void)
{
  return 0;
}

DDSMessage::MessageDataReader_ptr
DDSMessage::MessageDataReader::_duplicate (MessageDataReader_ptr obj)
{
  if (! ::CORBA::is_nil (obj))
    {
      obj->_add_ref ();
    }
  return obj;
}

void
DDSMessage::MessageDataReader::_tao_release (MessageDataReader_ptr obj)
{
  ::CORBA::release (obj);
}

::CORBA::Boolean
DDSMessage::MessageDataReader::_is_a (const char *value)
{
  if (
      ACE_OS::strcmp (
          value,
          "IDL:DDS/Entity:1.0"
        ) == 0 ||
      ACE_OS::strcmp (
          value,
          "IDL:DDS/DataReader:1.0"
        ) == 0 ||
      ACE_OS::strcmp (
          value,
          "IDL:OpenDDS/DCPS/DataReaderEx:1.0"
        ) == 0 ||
      ACE_OS::strcmp (
          value,
          "IDL:DDSMessage/MessageDataReader:1.0"
        ) == 0 ||
      ACE_OS::strcmp (
          value,
          "IDL:omg.org/CORBA/LocalObject:1.0"
        ) == 0 ||
      ACE_OS::strcmp (
          value,
          "IDL:omg.org/CORBA/Object:1.0"
        ) == 0
    )
    {
      return true; // success using local knowledge
    }
  else
    {
      return false;
    }
}

const char* DDSMessage::MessageDataReader::_interface_repository_id (void) const
{
  return "IDL:DDSMessage/MessageDataReader:1.0";
}

::CORBA::Boolean
DDSMessage::MessageDataReader::marshal (TAO_OutputCDR & /* cdr */)
{
  return false;
}
