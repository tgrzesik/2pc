using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization;
using System.ServiceModel;
using System.Text;

namespace WcfTransactionService
{
    [ServiceContract]
    public interface IService1
    {
        [OperationContract]
        [TransactionFlow(TransactionFlowOption.Mandatory)]
        bool Zlecenie_glosowania();

        [OperationContract]
        bool Zatwierdzanie_globalne();

        [OperationContract]
        bool Zaniechanie_globalne();
    }
}
