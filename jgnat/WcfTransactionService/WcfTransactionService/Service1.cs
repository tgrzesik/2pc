using System;
using System.Diagnostics;
using System.ServiceModel;

namespace WcfTransactionService
{
    [ServiceBehavior(TransactionIsolationLevel = System.Transactions.IsolationLevel.Serializable, InstanceContextMode = InstanceContextMode.Single)]
    public class Service1 : IService1
    {
        static int local_value;
        static int prev_value;
        static int global_value;

        [OperationBehavior(TransactionScopeRequired = true, TransactionAutoComplete = true)]
        public bool Zlecenie_glosowania()
        {
            try
            {
                prev_value = global_value;
                Random rnd = new Random();
                int c = 0;
                int b;
                for (int i = 0; i < 1000; i++)
                {
                    b = rnd.Next(0, 10000);
                    c = i*10000 / b;
                }
                local_value = c;
                return true;
            }
            catch (Exception e)
            {
                return false;
            }
        }

        public bool Zatwierdzanie_globalne()
        {
            try
            {
                global_value = local_value;
                return true;
            }
            catch (Exception e)
            {
                return false;
            }
        }

        public bool Zaniechanie_globalne()
        {
            try
            {
                global_value = prev_value;
                return true;
            }
            catch (Exception e)
            {
                return false;
            }
        }
    }
}
