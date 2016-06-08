using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Transactions;
using System.ServiceModel;
using System.ServiceModel.Discovery;

namespace WcfTransactionClient
{
    public class Program
    {
        static void Main(string[] args)
        {
            Console.WriteLine("--- ROZPOCZECIE TRANSAKCJI WCF ---");

            var result = StartOperation();

            if(result)
                Console.WriteLine("--- TRANSAKCJA ZAKONCZONA POMYSLNIE ---");
            else
                Console.WriteLine("--- TRANSAKCJA NIE ZOSTALA ZAKONCZONA POMYSLNIE ---");

            Console.WriteLine("--- KONIEC TRANSAKCJI WCF ---");
            Console.ReadLine();
        }

        public static bool StartOperation()
        {
            using (TransactionScope ts = new TransactionScope(TransactionScopeOption.RequiresNew))
            {
                try
                {
                    var wcfTransactSvc = new WcfTransactionService.Service1Client();
                    var serviceReference3 = new ServiceReference1.Service1Client();
                    bool res1, res2;
                    
                    try
                    {
                        var result1 = wcfTransactSvc.Zlecenie_glosowania();
                        res1 = result1;
                    }
                    catch(Exception e)
                    {
                        res1 = false;
                    }
                    try
                    {
                        var result2 = serviceReference3.Zlecenie_glosowania();
                        res2 = result2;
                    }
                    catch (Exception e)
                    {
                        res2 = false;
                    }
                    
                    if (!res1 || !res2)
                    {
                        try
                        {
                            wcfTransactSvc.Zaniechanie_globalne();
                        }
                        catch (Exception e) { };
                        try
                        {
                            serviceReference3.Zaniechanie_globalne();
                        }
                        catch (Exception e) { };

                        throw new Exception("");
                    }
                    wcfTransactSvc.Zatwierdzanie_globalne();
                    serviceReference3.Zatwierdzanie_globalne();
                    ts.Complete();
                }
                catch (Exception e)
                {
                    return false;
                }
                finally
                {
                    ts.Dispose();
                }
                return true;
            }
        }
    }
}
